package org.astrosearcher.classes.vizier;

import cds.savot.model.ResourceSet;
import cds.savot.model.SavotResource;
import cds.savot.model.SavotTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.astrosearcher.enums.cds.vizier.VizierServices;
import org.astrosearcher.utilities.VotableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents Vizier response used in final Response for user.
 * <p>
 * Class stores:
 * 1.) type of query
 * 2.) tables (data itself) - each parsed separately using VizierTable class
 *
 * @author Ľuboslav Halama
 */
@NoArgsConstructor
@Getter
public class VizierResponse {

    private static final Logger log = LoggerFactory.getLogger(VizierResponse.class);

    private VizierServices type;
    private List<VizierTable> tables = new ArrayList<>();

    public VizierResponse(VizierServices service, ResourceSet resources) {
        this.type = service;

        int debug_progress_counter = 0;
        log.debug("        Parsing the response...");

        // load and check every resource in response
        for (int resIndex = 0; resIndex < resources.getItemCount(); resIndex++) {
            SavotResource resource = (SavotResource) resources.getItemAt(resIndex);

            // load and check every table from current resource
            for (int tableIndex = 0; tableIndex < resource.getTableCount(); tableIndex++) {
                SavotTable table = (SavotTable) resource.getTables().getItemAt(tableIndex);

                // check if metadata were requested
                if (service == VizierServices.VIZIER_META) {
                    tables.add(new VizierTable(table, false));
                    continue;
                }

                // if table is not empty, store it into: List<VizierTable> tables
                if (!VotableUtils.isEmpty(table)) {
                    tables.add(new VizierTable(table));
                }
            }

            if (resIndex >= (debug_progress_counter + 1) * resources.getItemCount() / 4) {
                debug_progress_counter++;
                log.debug("            [  " + debug_progress_counter * 25 + "% ] resources parsed!");
            }
        }
            log.debug("            [ 100% ] resources parsed!");

    }

    public boolean isEmpty() {
        return tables.isEmpty();
    }
}
