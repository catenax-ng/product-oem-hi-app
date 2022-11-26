package net.catena_x.btp.hi.oem.common.model.dto.infoitem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.catena_x.btp.hi.oem.common.model.enums.HiInfoKey;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HIInfoItem {
    private HiInfoKey key;
    private String value;
}
