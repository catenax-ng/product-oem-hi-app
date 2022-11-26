package net.catena_x.btp.hi.oem.common.database.hi.tables.vehicle;

import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.items.HealthIndicatorOutput;
import net.catena_x.btp.hi.oem.common.database.hi.base.HITableBase;
import net.catena_x.btp.hi.oem.common.database.hi.tables.healthindicators.HealthIndicatorsDAO;
import net.catena_x.btp.hi.oem.common.database.hi.tables.healthindicators.HealthIndicatorsTableInternal;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.Vehicle;
import net.catena_x.btp.libraries.util.database.annotations.TransactionDefaultCreateNew;
import net.catena_x.btp.libraries.util.database.annotations.TransactionDefaultUseExisting;
import net.catena_x.btp.libraries.util.database.annotations.TransactionSerializableCreateNew;
import net.catena_x.btp.libraries.util.database.annotations.TransactionSerializableUseExisting;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class VehicleTableInternal extends HITableBase {
    @PersistenceContext EntityManager entityManager;

    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private HealthIndicatorsTableInternal healthindicatorsTable;

    @TransactionDefaultUseExisting
    public void insertVehicleExternalTransaction(@NotNull final Vehicle newVehicle)
            throws OemHIException {
        try {
            vehicleRepository.insert(newVehicle.getVehicleId(), newVehicle.getVan(),
                    newVehicle.getGearboxId(), newVehicle.getProductionDate());
        }
        catch(final Exception exception) {
            throw failed("Failed to insert vehicle!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void insertVehicleNewTransaction(@NotNull Vehicle newVehicle) throws OemHIException {
        insertVehicleExternalTransaction(newVehicle);
    }

    @TransactionDefaultUseExisting
    public void deleteAllExternalTransaction() throws OemHIException {
        try {
            vehicleRepository.deleteAll();
        } catch (final Exception exception) {
            throw failed("Deleting all vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void deleteAllNewTransaction() throws OemHIException {
        deleteAllExternalTransaction();
    }

    @TransactionSerializableUseExisting
    public void appendHealthindicatorsExternalTransaction(@NotNull String vehicleId,
                                                          @NotNull final HealthIndicatorOutput newHealthIndicators,
                                                          @NotNull final Instant calculationTimestamp,
                                                          @NotNull final long calculationSyncCounter)
            throws OemHIException {
        appendHealthindicators(getByIdWithHealthindicatorsExternalTransaction(vehicleId),
                               newHealthIndicators, calculationTimestamp, calculationSyncCounter);
    }

    @TransactionSerializableCreateNew
    public void appendHealthindicatorsNewTransaction(@NotNull String vehicleId,
                                                     @NotNull final HealthIndicatorOutput newHealthIndicators,
                                                     @NotNull final Instant calculationTimestamp,
                                                     @NotNull final long calculationSyncCounter)
            throws OemHIException {
        appendHealthindicatorsExternalTransaction(vehicleId, newHealthIndicators,
                                                  calculationTimestamp, calculationSyncCounter);
    }

    private void appendHealthindicators(@NotNull final VehicleWithHealthindicatorsDAO vehicle,
                                        @NotNull final HealthIndicatorOutput newHealthIndicators,
                                        @NotNull final Instant calculationTimestamp,
                                        @NotNull final long calculationSyncCounter)
            throws OemHIException {

        if(firstIsNewer(calculationSyncCounter, vehicle.healthIndicators())) {
            final String newHealthIndicatorsId = healthindicatorsTable.updateHealthIndicatorsGetIdExternalTransaction(
                    newHealthIndicators, vehicle.vehicle().getVehicleId(),
                    calculationTimestamp, calculationSyncCounter);

            try {
                vehicleRepository.updateNewestHealthindicatorsIdByVehicleId(vehicle.vehicle().getVehicleId(),
                                                                            newHealthIndicatorsId);
            } catch (final Exception exception) {
                throw failed("Appending health indicators failed!", exception);
            }
        }
    }

    private boolean firstIsNewer(@NotNull final long newCalculationSyncCounter,
                                 @Nullable final HealthIndicatorsDAO healthIndicators)
            throws OemHIException {

        if(healthIndicators == null) {
            return true;
        }

        return newCalculationSyncCounter > healthIndicators.getCalculationSyncCounter();
    }

    @TransactionDefaultUseExisting
    public void deleteByIdExternalTransaction(@NotNull final String id) throws OemHIException {
        try {
            vehicleRepository.deleteByVehilceId(id);
        }
        catch(final Exception exception) {
            throw failed("Deleting vehicle by id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void deleteByIdNewTransaction(@NotNull final String id) throws OemHIException {
        deleteByIdExternalTransaction(id);
    }

    @TransactionDefaultUseExisting
    public void deleteByVanExternalTransaction(@NotNull final String van) throws OemHIException {
        try {
            vehicleRepository.deleteByVan(van);
        }
        catch(final Exception exception) {
            throw failed("Deleting vehicle by van failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void deleteByVanNewTransaction(@NotNull final String van) throws OemHIException {
        deleteByVanExternalTransaction(van);
    }

    @TransactionDefaultUseExisting
    public VehicleDAO getByIdExternalTransaction(@NotNull final String vehicleId) throws OemHIException {
        try {
            return vehicleRepository.queryByVehicleId(vehicleId);
        } catch (final Exception exception) {
            throw failed("Querying vehicle by id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public VehicleDAO getByIdNewTransaction(@NotNull final String vehicleId) throws OemHIException {
        return getByIdExternalTransaction(vehicleId);
    }

    @TransactionDefaultUseExisting
    public VehicleWithHealthindicatorsDAO getByIdWithHealthindicatorsExternalTransaction(@NotNull final String vehicleId)
            throws OemHIException {
        try {
            final NativeQuery<Object[]> query = createJoinQueryVehicleHealthIndicators("v.vehicle_id=:vehicle_id")
                    .setParameter("vehicle_id", vehicleId);
            return getSingle(executeQueryVehicleHealthindicators(query));
        } catch(final Exception exception) {
            throw failed("Querying vehicle by id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public VehicleWithHealthindicatorsDAO getByIdWithHealthindicatorsNewTransaction(@NotNull final String vehicleId)
            throws OemHIException {
        return getByIdWithHealthindicatorsExternalTransaction(vehicleId);
    }

    @TransactionDefaultUseExisting
    public VehicleDAO findByVanExternalTransaction(@NotNull final String van) throws OemHIException {
        try {
            return vehicleRepository.queryByVan(van);
        } catch(final Exception exception) {
            throw failed("Querying vehicle by van failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public VehicleDAO findByVanNewTransaction(@NotNull final String van) throws OemHIException {
        return findByVanExternalTransaction(van);
    }

    @TransactionDefaultUseExisting
    public VehicleWithHealthindicatorsDAO getByVanWithHealthindicatorsExternalTransaction(@NotNull final String van)
            throws OemHIException {
        try {
            final NativeQuery<Object[]> query = createJoinQueryVehicleHealthIndicators("v.van=:van")
                    .setParameter("van", van);
            return getSingle(executeQueryVehicleHealthindicators(query));
        } catch(final Exception exception) {
            throw failed("Querying vehicle by van failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public VehicleWithHealthindicatorsDAO getByVanWithHealthindicatorsNewTransaction(
            @NotNull final String van) throws OemHIException {
        return getByVanWithHealthindicatorsExternalTransaction(van);
    }

    @TransactionDefaultUseExisting
    public VehicleDAO getByGearboxIdExternalTransaction(@NotNull final String gearboxId) throws OemHIException {
        try {
            return vehicleRepository.queryByGearboxId(gearboxId);
        } catch(final Exception exception) {
            throw failed("Querying vehicle by gearbox_id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public VehicleDAO getByGearboxIdNewTransaction(@NotNull final String gearboxId) throws OemHIException {
        return getByGearboxIdExternalTransaction(gearboxId);
    }

    @TransactionDefaultUseExisting
    public VehicleWithHealthindicatorsDAO getByGearboxIdWithHealthindicatorsExternalTransaction(
            @NotNull final String gearboxId) throws OemHIException {
        try {
            final NativeQuery<Object[]> query = createJoinQueryVehicleHealthIndicators(
                    "v.gearbox_id=:gearbox_id")
                    .setParameter("gearbox_id", gearboxId);
            return getSingle(executeQueryVehicleHealthindicators(query));
        } catch(final Exception exception) {
            throw failed("Querying vehicle by gearbox_id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public VehicleWithHealthindicatorsDAO getByGearboxIdWithHealthindicatorsNewTransaction(
            @NotNull final String gearboxId) throws OemHIException {
        return getByGearboxIdWithHealthindicatorsExternalTransaction(gearboxId);
    }

    private VehicleWithHealthindicatorsDAO queryAndAppendHealthindicators(final VehicleDAO vehicleFromDB)
            throws OemHIException {
        try {
            final String newestHealthindicatorsId = vehicleFromDB.getNewestHealthindicatorsId();

            if(newestHealthindicatorsId == null) {
                return new VehicleWithHealthindicatorsDAO(vehicleFromDB, null);
            }

            return new VehicleWithHealthindicatorsDAO(vehicleFromDB, healthindicatorsTable.getByIdExternalTransaction(
                                                                         vehicleFromDB.getNewestHealthindicatorsId()));
        } catch(final Exception exception) {
            throw failed("Querying vehicle by van failed!", exception);
        }
    }

    @TransactionDefaultUseExisting
    public List<VehicleDAO> getAllExternalTransaction() throws OemHIException {
        try {
            return vehicleRepository.queryAll();
        }
        catch(final Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<VehicleDAO> getAllNewTransaction() throws OemHIException {
        return getAllExternalTransaction();
    }

    @TransactionDefaultUseExisting
    public List<VehicleWithHealthindicatorsDAO> getAllWithHealthindicatorsExternalTransaction() throws OemHIException {
        try {
            final NativeQuery<Object[]> query = createJoinQueryVehicleHealthIndicators();
            return executeQueryVehicleHealthindicators(query);
        }
        catch(final Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<VehicleWithHealthindicatorsDAO> getAllWithHealthindicatorsNewTransaction() throws OemHIException {
        return getAllWithHealthindicatorsExternalTransaction();
    }

    @TransactionDefaultUseExisting
    public List<VehicleDAO> getUpdatedSinceExternalTransaction(@NotNull final Instant updatedSince)
            throws OemHIException {
        try {
            return vehicleRepository.queryUpdatedSince(updatedSince);
        }
        catch(final Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<VehicleDAO> getUpdatedSinceNewTransaction(@NotNull final Instant updatedSince)
            throws OemHIException {
        return getUpdatedSinceExternalTransaction(updatedSince);
    }

    @TransactionDefaultUseExisting
    public List<VehicleDAO> getProducedBetweenExternalTransaction(@NotNull final Instant producedSince,
                                                                @NotNull final Instant producedUntil)
            throws OemHIException {
        try {
            return vehicleRepository.queryByProductionDate(producedSince, producedUntil);
        }
        catch(final Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<VehicleDAO> getProducedBetweenNewTransaction(@NotNull final Instant producedSince,
                                                             @NotNull final Instant producedUntil)
            throws OemHIException {
        return getProducedBetweenExternalTransaction(producedSince, producedUntil);
    }

    @TransactionDefaultUseExisting
    public List<VehicleWithHealthindicatorsDAO> getProducedBetweenWithHealthindicatorsExternalTransaction(
            @NotNull final Instant producedSince, @NotNull final Instant producedUntil) throws OemHIException {
        try {
            final NativeQuery<Object[]> query = createJoinQueryVehicleHealthIndicators(
                    "v.production_date>=:produced_since AND v.production_date<=:produced_until")
                    .setParameter("produced_since", producedSince)
                    .setParameter("produced_until", producedUntil);
            return executeQueryVehicleHealthindicators(query);
        }
        catch(final Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<VehicleWithHealthindicatorsDAO> getProducedBetweenWithHealthindicatorsNewTransaction(
            @NotNull final Instant producedSince, @NotNull final Instant producedUntil) throws OemHIException {
        return getProducedBetweenWithHealthindicatorsExternalTransaction(producedSince, producedUntil);
    }

    private List<VehicleWithHealthindicatorsDAO> executeQueryVehicleHealthindicators(
            @NotNull final NativeQuery<Object[]> query) throws OemHIException {
        try {
            final List<Object[]> results = query.list();
            final List<VehicleWithHealthindicatorsDAO> vehicles = new ArrayList<>(results.size());

            results.stream().forEach((record) -> vehicles.add(
                    new VehicleWithHealthindicatorsDAO((VehicleDAO)record[0], (HealthIndicatorsDAO)record[1])));

            return vehicles;
        } catch(final Exception exception) {
            throw failed("Executing query failed!", exception);
        }
    }

    private NativeQuery<Object[]> createJoinQueryVehicleHealthIndicators() throws OemHIException {
        return createJoinQueryVehicleHealthIndicators(null);
    }

    private NativeQuery<Object[]> createJoinQueryVehicleHealthIndicators(@Nullable final String where)
            throws OemHIException {
        String query = "SELECT {v.*}, {h.*} FROM vehicles v " +
                "LEFT OUTER JOIN healthindicators h ON v.newest_healthindicators_id = h.id";

        if(where != null) {
            query += " WHERE " + where;
        }

        return createQuery(query).addEntity("v", VehicleDAO.class)
                .addEntity("t", HealthIndicatorsDAO.class);
    }

    private NativeQuery<Object[]> createQuery(@NotNull final String query) throws OemHIException {
        try {
            return (NativeQuery<Object[]>)((Session)this.entityManager.getDelegate()).createSQLQuery(query);
        }
        catch(final Exception exception) {
            throw failed("Initializing query failed!", exception);
        }
    }

    private VehicleWithHealthindicatorsDAO getSingle(@Nullable final List<VehicleWithHealthindicatorsDAO> vehicles)
            throws OemHIException {
        if(vehicles == null) {
            return null;
        }

        if(vehicles.isEmpty()) {
            return null;
        }

        if(vehicles.size() > 1) {
            throw failed("Found more than one vehicle with the same id!");
        }

        return vehicles.get(0);
    }
}
