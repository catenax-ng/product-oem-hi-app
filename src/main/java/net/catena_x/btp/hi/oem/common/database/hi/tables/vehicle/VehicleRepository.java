package net.catena_x.btp.hi.oem.common.database.hi.tables.vehicle;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public interface VehicleRepository extends Repository<VehicleDAO, String> {
    @Modifying void insert(@Param("vehicle_id") @NotNull final String vehicleId,
                           @Param("van") @NotNull final String van,
                           @Param("gearbox_id") @NotNull final String gearboxId,
                           @Param("production_date") @NotNull final Instant productionDate);
    @Modifying void updateNewestHealthindicatorsIdByVehicleId(
            @Param("vehicle_id") @NotNull final String vehicleId,
            @Param("newest_healthindicators_id") @NotNull final String newestHealthindicatorsId);
    @Modifying void deleteAll();
    @Modifying void deleteByVehilceId(@Param("vehicle_id") @NotNull final String vehicleId);
    @Modifying void deleteByVan(@Param("van") @NotNull final String van);
    List<VehicleDAO> queryAll();
    VehicleDAO queryByVehicleId(@Param("vehicle_id") @NotNull final String vehicleId);
    VehicleDAO queryByVan(@Param("van") @NotNull final String van);
    VehicleDAO queryByGearboxId(@Param("gearbox_id") @NotNull final String gearboxId);
    List<VehicleDAO> queryUpdatedSince(@Param("updated_since") @NotNull final Instant updatedSince);
    List<VehicleDAO> queryByProductionDate(@Param("produced_since") @NotNull final Instant producedSince,
                                           @Param("produced_until") @NotNull final Instant producedUntil);
}
