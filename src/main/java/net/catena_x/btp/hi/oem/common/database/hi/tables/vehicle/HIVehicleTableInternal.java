package net.catena_x.btp.hi.oem.common.database.hi.tables.vehicle;

import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.items.HealthIndicatorOutput;
import net.catena_x.btp.hi.oem.common.database.hi.base.HITableBase;
import net.catena_x.btp.hi.oem.common.database.hi.tables.healthindicators.HIHealthIndicatorsDAO;
import net.catena_x.btp.hi.oem.common.database.hi.tables.healthindicators.HIHealthIndicatorsTableInternal;
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
public class HIVehicleTableInternal extends HITableBase {
    @PersistenceContext EntityManager entityManager;

    @Autowired private HIVehicleRepository HIVehicleRepository;
    @Autowired private HIHealthIndicatorsTableInternal healthindicatorsTable;

    @TransactionDefaultUseExisting
    public void insertVehicleExternalTransaction(@NotNull final Vehicle newVehicle)
            throws OemHIException {
        try {
            HIVehicleRepository.insert(newVehicle.getVehicleId(), newVehicle.getVan(),
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
            HIVehicleRepository.deleteAll();
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
        appendHealthindicators(getByIdWithHealthIndicatorsExternalTransaction(vehicleId),
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

    private void appendHealthindicators(@NotNull final HIVehicleWithHealthIndicatorsDAO vehicle,
                                        @NotNull final HealthIndicatorOutput newHealthIndicators,
                                        @NotNull final Instant calculationTimestamp,
                                        @NotNull final long calculationSyncCounter)
            throws OemHIException {

        if(firstIsNewer(calculationSyncCounter, vehicle.healthIndicators())) {
            final String newHealthIndicatorsId = healthindicatorsTable.updateHealthIndicatorsGetIdExternalTransaction(
                    newHealthIndicators, vehicle.vehicle().getVehicleId(),
                    calculationTimestamp, calculationSyncCounter);

            try {
                HIVehicleRepository.updateNewestHealthindicatorsIdByVehicleId(vehicle.vehicle().getVehicleId(),
                                                                            newHealthIndicatorsId);
            } catch (final Exception exception) {
                throw failed("Appending health indicators failed!", exception);
            }
        }
    }

    private boolean firstIsNewer(@NotNull final long newCalculationSyncCounter,
                                 @Nullable final HIHealthIndicatorsDAO healthIndicators)
            throws OemHIException {

        if(healthIndicators == null) {
            return true;
        }

        return newCalculationSyncCounter > healthIndicators.getCalculationSyncCounter();
    }

    @TransactionDefaultUseExisting
    public void deleteByIdExternalTransaction(@NotNull final String id) throws OemHIException {
        try {
            HIVehicleRepository.deleteByVehilceId(id);
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
            HIVehicleRepository.deleteByVan(van);
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
    public HIVehicleDAO getByIdExternalTransaction(@NotNull final String vehicleId) throws OemHIException {
        try {
            return HIVehicleRepository.queryByVehicleId(vehicleId);
        } catch (final Exception exception) {
            throw failed("Querying vehicle by id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public HIVehicleDAO getByIdNewTransaction(@NotNull final String vehicleId) throws OemHIException {
        return getByIdExternalTransaction(vehicleId);
    }

    @TransactionDefaultUseExisting
    public HIVehicleWithHealthIndicatorsDAO getByIdWithHealthIndicatorsExternalTransaction(@NotNull final String vehicleId)
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
    public HIVehicleWithHealthIndicatorsDAO getByIdWithHealthIndicatorsNewTransaction(@NotNull final String vehicleId)
            throws OemHIException {
        return getByIdWithHealthIndicatorsExternalTransaction(vehicleId);
    }

    @TransactionDefaultUseExisting
    public HIVehicleDAO getByVanExternalTransaction(@NotNull final String van) throws OemHIException {
        try {
            return HIVehicleRepository.queryByVan(van);
        } catch(final Exception exception) {
            throw failed("Querying vehicle by van failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public HIVehicleDAO getByVanNewTransaction(@NotNull final String van) throws OemHIException {
        return getByVanExternalTransaction(van);
    }

    @TransactionDefaultUseExisting
    public HIVehicleWithHealthIndicatorsDAO getByVanWithHealthIndicatorsExternalTransaction(@NotNull final String van)
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
    public HIVehicleWithHealthIndicatorsDAO getByVanWithHealthIndicatorsNewTransaction(
            @NotNull final String van) throws OemHIException {
        return getByVanWithHealthIndicatorsExternalTransaction(van);
    }

    @TransactionDefaultUseExisting
    public HIVehicleDAO getByGearboxIdExternalTransaction(@NotNull final String gearboxId) throws OemHIException {
        try {
            return HIVehicleRepository.queryByGearboxId(gearboxId);
        } catch(final Exception exception) {
            throw failed("Querying vehicle by gearbox_id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public HIVehicleDAO getByGearboxIdNewTransaction(@NotNull final String gearboxId) throws OemHIException {
        return getByGearboxIdExternalTransaction(gearboxId);
    }

    @TransactionDefaultUseExisting
    public HIVehicleWithHealthIndicatorsDAO getByGearboxIdWithHealthIndicatorsExternalTransaction(
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
    public HIVehicleWithHealthIndicatorsDAO getByGearboxIdWithHealthIndicatorsNewTransaction(
            @NotNull final String gearboxId) throws OemHIException {
        return getByGearboxIdWithHealthIndicatorsExternalTransaction(gearboxId);
    }

    private HIVehicleWithHealthIndicatorsDAO queryAndAppendHealthindicators(final HIVehicleDAO vehicleFromDB)
            throws OemHIException {
        try {
            final String newestHealthindicatorsId = vehicleFromDB.getNewestHealthindicatorsId();

            if(newestHealthindicatorsId == null) {
                return new HIVehicleWithHealthIndicatorsDAO(vehicleFromDB, null);
            }

            return new HIVehicleWithHealthIndicatorsDAO(vehicleFromDB, healthindicatorsTable.getByIdExternalTransaction(
                                                                         vehicleFromDB.getNewestHealthindicatorsId()));
        } catch(final Exception exception) {
            throw failed("Querying vehicle by van failed!", exception);
        }
    }

    @TransactionDefaultUseExisting
    public List<HIVehicleDAO> getAllExternalTransaction() throws OemHIException {
        try {
            return HIVehicleRepository.queryAll();
        }
        catch(final Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIVehicleDAO> getAllNewTransaction() throws OemHIException {
        return getAllExternalTransaction();
    }

    @TransactionDefaultUseExisting
    public List<HIVehicleWithHealthIndicatorsDAO> getAllWithHealthIndicatorsExternalTransaction() throws OemHIException {
        try {
            final NativeQuery<Object[]> query = createJoinQueryVehicleHealthIndicators();
            return executeQueryVehicleHealthindicators(query);
        }
        catch(final Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIVehicleWithHealthIndicatorsDAO> getAllWithHealthIndicatorsNewTransaction() throws OemHIException {
        return getAllWithHealthIndicatorsExternalTransaction();
    }

    @TransactionDefaultUseExisting
    public List<HIVehicleDAO> getUpdatedSinceExternalTransaction(@NotNull final Instant updatedSince)
            throws OemHIException {
        try {
            return HIVehicleRepository.queryUpdatedSince(updatedSince);
        }
        catch(final Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIVehicleDAO> getUpdatedSinceNewTransaction(@NotNull final Instant updatedSince)
            throws OemHIException {
        return getUpdatedSinceExternalTransaction(updatedSince);
    }

    @TransactionDefaultUseExisting
    public List<HIVehicleWithHealthIndicatorsDAO> getUpdatedSinceWithHealthIndicatorsExternalTransaction(
            @NotNull final Instant updatedSince) throws OemHIException {
        try {
            final NativeQuery<Object[]> query = createJoinQueryVehicleHealthIndicators(
                    "v.update_timestamp>=:updated_since")
                    .setParameter("updated_since", updatedSince);
            return executeQueryVehicleHealthindicators(query);
        }
        catch(final Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIVehicleWithHealthIndicatorsDAO> getUpdatedSinceWithHealthIndicatorsNewTransaction(
            @NotNull final Instant updatedSince) throws OemHIException {
        return getUpdatedSinceWithHealthIndicatorsExternalTransaction(updatedSince);
    }

    @TransactionDefaultUseExisting
    public List<HIVehicleDAO> getProducedBetweenExternalTransaction(@NotNull final Instant producedSince,
                                                                    @NotNull final Instant producedUntil)
            throws OemHIException {
        try {
            return HIVehicleRepository.queryByProductionDate(producedSince, producedUntil);
        }
        catch(final Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<HIVehicleDAO> getProducedBetweenNewTransaction(@NotNull final Instant producedSince,
                                                               @NotNull final Instant producedUntil)
            throws OemHIException {
        return getProducedBetweenExternalTransaction(producedSince, producedUntil);
    }

    @TransactionDefaultUseExisting
    public List<HIVehicleWithHealthIndicatorsDAO> getProducedBetweenWithHealthIndicatorsExternalTransaction(
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
    public List<HIVehicleWithHealthIndicatorsDAO> getProducedBetweenWithHealthIndicatorsNewTransaction(
            @NotNull final Instant producedSince, @NotNull final Instant producedUntil) throws OemHIException {
        return getProducedBetweenWithHealthIndicatorsExternalTransaction(producedSince, producedUntil);
    }

    private List<HIVehicleWithHealthIndicatorsDAO> executeQueryVehicleHealthindicators(
            @NotNull final NativeQuery<Object[]> query) throws OemHIException {
        try {
            final List<Object[]> results = query.list();
            final List<HIVehicleWithHealthIndicatorsDAO> vehicles = new ArrayList<>(results.size());

            results.stream().forEach((record) -> vehicles.add(
                    new HIVehicleWithHealthIndicatorsDAO((HIVehicleDAO)record[0], (HIHealthIndicatorsDAO)record[1])));

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

        return createQuery(query).addEntity("v", HIVehicleDAO.class)
                .addEntity("t", HIHealthIndicatorsDAO.class);
    }

    private NativeQuery<Object[]> createQuery(@NotNull final String query) throws OemHIException {
        try {
            return (NativeQuery<Object[]>)((Session)this.entityManager.getDelegate()).createSQLQuery(query);
        }
        catch(final Exception exception) {
            throw failed("Initializing query failed!", exception);
        }
    }

    private HIVehicleWithHealthIndicatorsDAO getSingle(@Nullable final List<HIVehicleWithHealthIndicatorsDAO> vehicles)
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
