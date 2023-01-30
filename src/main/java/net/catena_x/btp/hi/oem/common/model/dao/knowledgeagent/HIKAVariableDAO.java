package net.catena_x.btp.hi.oem.common.model.dao.knowledgeagent;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HIKAVariableDAO {
    private String type;
    private String value;
    private String datatype;
}
