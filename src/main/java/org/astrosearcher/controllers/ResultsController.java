package org.astrosearcher.controllers;

import com.google.gson.Gson;
import org.astrosearcher.utilities.ConnectionUtils;
import org.astrosearcher.validators.FileValidator;
import org.astrosearcher.classes.ResponseData;
import org.astrosearcher.classes.constants.Limits;
import org.astrosearcher.classes.constants.cds.SimbadConstants;
import org.astrosearcher.classes.constants.cds.VizierConstants;
import org.astrosearcher.classes.constants.messages.InformationMSG;
import org.astrosearcher.classes.simbad.SimbadFlux;
import org.astrosearcher.enums.VizierCatalogueSearch;
import org.astrosearcher.enums.cds.simbad.SimbadServices;
import org.astrosearcher.enums.SearchType;
import org.astrosearcher.models.SearchFormInput;
import org.astrosearcher.utilities.SearchEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@EnableAsync
public class ResultsController {

    @Autowired
    private SearchEngine engine;

    @Autowired
    private FileValidator fileValidator;

    private void handleFail(String message, Model model) {
        model.addAttribute("errorMSG",      message);
        model.addAttribute("searchOptions", SearchType.values());
        model.addAttribute("vizierOptions", VizierCatalogueSearch.values());
    }

    private void processMastResponse(ResponseData responseData, Model model) {

        if (responseData.containsMastResponse()) {
            model.addAttribute("mastFields", responseData.getMastResponse().getFields());
            model.addAttribute("mastData", responseData.getMastResponse().getData());
        } else {
            model.addAttribute("mastDataMSG", InformationMSG.NO_MAST_DATA);
        }
        model.addAttribute("containsMAST", responseData.containsMastResponse());
    }

    private void processVizierResponse(ResponseData responseData, Model model) {

        if (responseData.containsVizierResponse()) {
            model.addAttribute("vizierType",   responseData.getVizierResponse().getType()   );
            model.addAttribute("vizierTables", responseData.getVizierResponse().getTables() );
        } else {
            model.addAttribute("vizierDataMSG", InformationMSG.NO_VIZIER_DATA);
        }
        model.addAttribute("containsVizier", responseData.containsVizierResponse());
    }

    private void processSimbadResponse(ResponseData responseData, Model model) {

        if (responseData.containsSimbadResponse()) {

            model.addAttribute("simbadFields",   responseData.getSimbadResponse().getFields()      );
            model.addAttribute("simbadType",     responseData.getSimbadResponse().getType().name() );
            model.addAttribute("simbadResponse", responseData.getSimbadResponse());

            if (responseData.getSimbadResponse().getType() == SimbadServices.SIMBAD_ID) {
                model.addAttribute("fluxFields", SimbadFlux.class.getFields());
            }
        } else {
            model.addAttribute("simbadDataMSG", InformationMSG.NO_SIMBAD_DATA);
        }
        model.addAttribute("containsSimbad", responseData.containsSimbadResponse());
    }

    private String processResponse(ResponseData responseData, Model model) {

        if (responseData.isEmpty()) {
            handleFail(InformationMSG.NO_DATA_AT_ALL, model);
            return "index";
        }

        processMastResponse(responseData, model);
        processVizierResponse(responseData, model);
        processSimbadResponse(responseData, model);
        return "results";
    }

    @PostMapping("results")
    public String postSearch(@ModelAttribute @Valid SearchFormInput input,
                             Errors errors, Model model) {


        fileValidator.validate(input, errors);
        if (errors.hasErrors()) {
            handleFail(errors.getAllErrors().get(0).getDefaultMessage(), model);
            return "index";
        }
        else if (errors.hasFieldErrors()) {
            handleFail(errors.getFieldError().getDefaultMessage(), model);
            return "index";
        } else if (errors.hasGlobalErrors()) {
            handleFail(errors.getGlobalError().getDefaultMessage(), model);
            return "index";
        }

        ResponseData responseData;
        try {
            responseData = engine.process(input);
        } catch (IllegalArgumentException iae) {
            handleFail(iae.getMessage(), model);
            return "index";
        } catch (Exception e) {
            e.printStackTrace();
            return "index";
        }

        return processResponse(responseData, model);
    }

    @GetMapping("results")
    public String getSearch(@RequestParam String id, Model model) {

        ResponseData responseData;

        try {
            responseData = engine.process(new SearchFormInput(
                    SearchType.ID_NAME.toString(),
                    id,
                    SimbadConstants.DEFAULT_FORMAT,
                    Limits.DEFAULT_RADIUS,
                    Limits.DEFAULT_PAGE, Limits.DEFAULT_PAGESIZE,
                    VizierCatalogueSearch.code.toString(),
                    VizierConstants.DEFAULT_CATALOG,
                    null,
                    true, true, true));
        } catch (IllegalArgumentException iae) {
            handleFail(iae.getMessage(), model);
            return "index";
        } catch (Exception e) {
            e.printStackTrace();
            return "index";
        }

        return processResponse(responseData, model);
    }

    @GetMapping("results/json")
    public ResponseEntity<Object> getSearchJson(@RequestParam SearchFormInput searchFormInput) {
        Gson gson = new Gson();
        ResponseData responseData;

        try {
            responseData = engine.process(searchFormInput);
        } catch (IllegalArgumentException iae) {
            return new ResponseEntity<>(gson.toJson(iae.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(gson.toJson(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (responseData.isEmpty()) {
            return new ResponseEntity<>(gson.toJson(InformationMSG.NO_DATA_AT_ALL), HttpStatus.NO_CONTENT);
        }

        return ConnectionUtils.prepareJsonResponseEntity(responseData);
    }

    @GetMapping(value = "results/json", params = {"id", "queryMast", "queryVizier", "querySimbad"})
    public ResponseEntity<Object> getSearchJson(@RequestParam String id, @RequestParam int queryMast,
                                      @RequestParam int queryVizier, @RequestParam int querySimbad) {

        ResponseData finalResponse;

        // check if only metadata are requested from Vizier
        if (queryVizier == 2) {
            finalResponse = engine.process(new SearchFormInput(
                    SearchType.ID_NAME,
                    id,
                    queryMast == 1,
                    false,
                    querySimbad == 1));
            ResponseData vizierMetadata = engine.getVizierCatalogMetadataForObjectIdJson(id);

            if (vizierMetadata.containsVizierResponse()) {
                finalResponse.setVizierResponse(vizierMetadata.getVizierResponse());
            }
            return ConnectionUtils.prepareJsonResponseEntity(finalResponse);
        }

        return getSearchJson(new SearchFormInput(
                SearchType.ID_NAME,
                id,
                queryMast == 1,
                queryVizier == 1,
                querySimbad == 1));

    }

    @GetMapping(value = "results/json", params = "id")
    public ResponseEntity<Object> getSearchJson(@RequestParam String id) {
        return getSearchJson(new SearchFormInput(SearchType.ID_NAME, id));
    }

    @GetMapping(value = "results/json", params = {"ra", "dec"})
    public ResponseEntity<Object> getSearchJson(@RequestParam String ra, @RequestParam String dec) {
        return getSearchJson(new SearchFormInput(SearchType.POSITION, ra + " " + dec));
    }

    @GetMapping(value = "results/json", params = {"id", "vizierInputType", "vizier"})
    public ResponseEntity<Object> getSearchJson(@RequestParam String id,
                                                @RequestParam String vizierInputType, @RequestParam String vizier) {
        return getSearchJson(new SearchFormInput(SearchType.ID_NAME, id, vizierInputType, vizier));
    }

    @GetMapping(value = "results/json", params = {"ra", "dec", "vizierInputType", "vizier"})
    public ResponseEntity<Object> getSearchJson(@RequestParam String ra, @RequestParam String dec,
                                                @RequestParam String vizierInputType, @RequestParam String vizier) {
        return getSearchJson(new SearchFormInput(SearchType.POSITION, ra + " " + dec, vizierInputType, vizier));
    }

    @GetMapping(value = "results/vizier/cats", params = "id")
    @ResponseBody
    public ResponseData getVizierCatalogMetadataForObjectIdJson(@RequestParam String id) {
        return engine.getVizierCatalogMetadataForObjectIdJson(id);
    }
}
