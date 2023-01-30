package net.catena_x.btp.hi.oem.common.model.dao.knowledgeagent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@NoArgsConstructor
public class HIKAOutputsDAO extends HIKAExchangeDAOBase<HIKAOutputBindingDAO> {
    public HIKAOutputsDAO(@Nullable final HIKAInputsHeaderDAO head,
                         @Nullable final HIKAResultsDAO<HIKAOutputBindingDAO> results) {
        super(head, results);
    }
}