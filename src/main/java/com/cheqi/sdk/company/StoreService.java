package com.cheqi.sdk.company;

import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.models.company.CreateStoreRequest;
import com.cheqi.sdk.models.company.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Service for store/location management operations.
 *
 * Enables management of stores (locations) for a company. Requires WRITE_STORES scope.
 *
 * <h3>Example usage:</h3>
 * <pre>
 * CreateStoreRequest storeRequest = CreateStoreRequest.builder()
 *     .storeName("My Store Amsterdam")
 *     .storeCode("STORE-001")
 *     .address(Address.builder()
 *         .streetName("Kalverstraat 1")
 *         .cityName("Amsterdam")
 *         .postalZone("1012NX")
 *         .countryIsoCode("NL")
 *         .build())
 *     .phoneNumber("+31201234567")
 *     .email("amsterdam@mystore.nl")
 *     .openingHours("Mon-Sat: 09:00-18:00")
 *     .build();
 *
 * Store store = sdk.getStoreService()
 *     .createStore(companyId, storeRequest, accessToken);
 *
 * // List all stores
 * List&lt;Store&gt; stores = sdk.getStoreService()
 *     .getStores(companyId, accessToken);
 * </pre>
 */
public class StoreService {
    private static final Logger logger = LoggerFactory.getLogger(StoreService.class);

    private final CheqiApiClient apiClient;

    public StoreService(CheqiApiClient apiClient) {
        this.apiClient = Objects.requireNonNull(apiClient, "apiClient cannot be null");
        logger.info("StoreService initialized successfully");
    }

    /**
     * Creates a new store/location for a company.
     *
     * @param companyId The company ID to create the store for
     * @param request Store creation request with store details
     * @param accessToken OAuth access token with WRITE_STORES scope
     * @return Created Store with ID and timestamps
     * @throws CheqiSDKException if creation fails
     */
    public Store createStore(UUID companyId, CreateStoreRequest request, String accessToken) throws CheqiSDKException {
        validateCompanyId(companyId);
        validateAccessToken(accessToken);

        if (request == null) {
            throw new CheqiSDKException("Store request cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        if (!request.isValid()) {
            throw new CheqiSDKException("Invalid store request: " + String.join(", ", request.getValidationErrors()),
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        logger.info("Creating store '{}' for company {}", request.getStoreName(), companyId);

        try {
            Store store = apiClient.createStore(companyId, request, accessToken);
            logger.info("Store created successfully: storeId={}", store.getId());
            return store;
        } catch (Exception e) {
            logger.error("Failed to create store: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to create store: " + e.getMessage(), e);
        }
    }

    /**
     * Gets all stores for a company.
     *
     * @param companyId The company ID
     * @param accessToken OAuth access token
     * @return List of stores
     * @throws CheqiSDKException if retrieval fails
     */
    public List<Store> getStores(UUID companyId, String accessToken) throws CheqiSDKException {
        validateCompanyId(companyId);
        validateAccessToken(accessToken);

        logger.debug("Getting stores for company {}", companyId);

        try {
            return apiClient.getStores(companyId, null, accessToken);
        } catch (Exception e) {
            logger.error("Failed to get stores: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to get stores: " + e.getMessage(), e);
        }
    }

    /**
     * Gets active stores for a company.
     *
     * @param companyId The company ID
     * @param accessToken OAuth access token
     * @return List of active stores
     * @throws CheqiSDKException if retrieval fails
     */
    public List<Store> getActiveStores(UUID companyId, String accessToken) throws CheqiSDKException {
        validateCompanyId(companyId);
        validateAccessToken(accessToken);

        logger.debug("Getting active stores for company {}", companyId);

        try {
            return apiClient.getStores(companyId, true, accessToken);
        } catch (Exception e) {
            logger.error("Failed to get active stores: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to get active stores: " + e.getMessage(), e);
        }
    }

    /**
     * Gets a specific store by ID.
     *
     * @param companyId The company ID
     * @param storeId The store ID
     * @param accessToken OAuth access token
     * @return Store details
     * @throws CheqiSDKException if retrieval fails
     */
    public Store getStore(UUID companyId, UUID storeId, String accessToken) throws CheqiSDKException {
        validateCompanyId(companyId);
        validateStoreId(storeId);
        validateAccessToken(accessToken);

        logger.debug("Getting store {} for company {}", storeId, companyId);

        try {
            return apiClient.getStore(companyId, storeId, accessToken);
        } catch (Exception e) {
            logger.error("Failed to get store: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to get store: " + e.getMessage(), e);
        }
    }

    /**
     * Updates a store.
     *
     * @param companyId The company ID
     * @param storeId The store ID to update
     * @param request Updated store details
     * @param accessToken OAuth access token with WRITE_STORES scope
     * @return Updated store
     * @throws CheqiSDKException if update fails
     */
    public Store updateStore(UUID companyId, UUID storeId, CreateStoreRequest request, String accessToken) throws CheqiSDKException {
        validateCompanyId(companyId);
        validateStoreId(storeId);
        validateAccessToken(accessToken);

        if (request == null) {
            throw new CheqiSDKException("Store request cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        logger.info("Updating store {} for company {}", storeId, companyId);

        try {
            Store store = apiClient.updateStore(companyId, storeId, request, accessToken);
            logger.info("Store updated successfully: storeId={}", store.getId());
            return store;
        } catch (Exception e) {
            logger.error("Failed to update store: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to update store: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a store.
     *
     * @param companyId The company ID
     * @param storeId The store ID to delete
     * @param accessToken OAuth access token with WRITE_STORES scope
     * @throws CheqiSDKException if deletion fails
     */
    public void deleteStore(UUID companyId, UUID storeId, String accessToken) throws CheqiSDKException {
        validateCompanyId(companyId);
        validateStoreId(storeId);
        validateAccessToken(accessToken);

        logger.info("Deleting store {} for company {}", storeId, companyId);

        try {
            apiClient.deleteStore(companyId, storeId, accessToken);
            logger.info("Store deleted successfully: storeId={}", storeId);
        } catch (Exception e) {
            logger.error("Failed to delete store: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to delete store: " + e.getMessage(), e);
        }
    }

    /**
     * Activates a store.
     *
     * @param companyId The company ID
     * @param storeId The store ID to activate
     * @param accessToken OAuth access token with WRITE_STORES scope
     * @throws CheqiSDKException if activation fails
     */
    public void activateStore(UUID companyId, UUID storeId, String accessToken) throws CheqiSDKException {
        validateCompanyId(companyId);
        validateStoreId(storeId);
        validateAccessToken(accessToken);

        logger.info("Activating store {} for company {}", storeId, companyId);

        try {
            apiClient.activateStore(companyId, storeId, accessToken);
            logger.info("Store activated successfully: storeId={}", storeId);
        } catch (Exception e) {
            logger.error("Failed to activate store: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to activate store: " + e.getMessage(), e);
        }
    }

    /**
     * Deactivates a store.
     *
     * @param companyId The company ID
     * @param storeId The store ID to deactivate
     * @param accessToken OAuth access token with WRITE_STORES scope
     * @throws CheqiSDKException if deactivation fails
     */
    public void deactivateStore(UUID companyId, UUID storeId, String accessToken) throws CheqiSDKException {
        validateCompanyId(companyId);
        validateStoreId(storeId);
        validateAccessToken(accessToken);

        logger.info("Deactivating store {} for company {}", storeId, companyId);

        try {
            apiClient.deactivateStore(companyId, storeId, accessToken);
            logger.info("Store deactivated successfully: storeId={}", storeId);
        } catch (Exception e) {
            logger.error("Failed to deactivate store: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to deactivate store: " + e.getMessage(), e);
        }
    }

    private void validateCompanyId(UUID companyId) throws CheqiSDKException {
        if (companyId == null) {
            throw new CheqiSDKException("Company ID cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }
    }

    private void validateStoreId(UUID storeId) throws CheqiSDKException {
        if (storeId == null) {
            throw new CheqiSDKException("Store ID cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }
    }

    private void validateAccessToken(String accessToken) throws CheqiSDKException {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new CheqiSDKException("Access token cannot be null or empty",
                    CheqiSDKException.ErrorCodes.AUTHENTICATION_FAILED, 401, null);
        }
    }

}
