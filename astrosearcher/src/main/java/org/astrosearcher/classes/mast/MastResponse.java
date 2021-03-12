package org.astrosearcher.classes.mast;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.astrosearcher.classes.mast.services.caom.cone.CaomFields;
import org.astrosearcher.classes.mast.services.caom.cone.ResponseForReqByPos;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
public class MastResponse {
    private List<CaomFields> fields = new ArrayList<>();

    private List<JsonObject> data = new ArrayList<>();

    public MastResponse() {}

    public MastResponse(@NotNull ResponseForReqByPos response) {
        this(response.getFields(), response.getData());
    }

    public MastResponse(List<JsonObject> responseFields, List<JsonObject> data) {

        if (responseFields == null) {
            return;
        }

        for (CaomFields caomField : CaomFields.values()) {
            for (JsonObject field : responseFields) {
                try {
                    if ( !caomField.equals(field.get("name").getAsString()) ) {
                        continue;
                    }

                    if ( caomField.isVisible() ) {
                        this.fields.add(caomField);
                    }
                    break;
                } catch (NullPointerException npe) {
                    throw new NullPointerException(
                            "Invalid field name in MAST response (null) or name has not been found at all." + npe
                    );
                } catch (IllegalArgumentException iae) {
                    throw new IllegalArgumentException(
                            "Field name in MAST response was not found in pre-defined MAST-fields Enum class." + iae
                    );
                }
            }
        }

        this.data = data;
    }
}
