package net.catena_x.btp.hi.oem.common.model.dao.knowledgeagent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HIKABinding {
    private HIKAVariable vin;
    private HIKAVariable aggregate;
}
