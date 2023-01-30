package net.catena_x.btp.hi.oem.common.model.dao.knowledgeagent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HIKAInputBindingDAO {
    private HIKAVariableDAO van;
    private HIKAVariableDAO aggregate;
    private HIKAVariableDAO healthType;
    private HIKAVariableDAO adaptionValues;
}
