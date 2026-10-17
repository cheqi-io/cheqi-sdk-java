package com.cheqi.sdk.company;

import com.cheqi.sdk.exceptions.CheqiSDKException;
import com.cheqi.sdk.http.CheqiApiClient;
import com.cheqi.sdk.http.RequestOptions;
import com.cheqi.sdk.models.generated.CreateStoreRequest;
import com.cheqi.sdk.models.generated.StoreDTO;
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
 *     .createStore(companyId, storeRequest, RequestOptions.withAccessToken(accessToken));
 *
 * // List all stores
 * List&lt;Store&gt; stores = sdk.getStoreService()
 *     .getStores(companyId, RequestOptions.withAccessToken(accessToken));
 * </pre>
 */
public class StoreService {
    private static final Logger logger = LoggerFactory.getLogger(StoreService.class);

    private final CheqiApiClient apiClient;

    public StoreService(CheqiApiClient apiClient) {
        this.apiClient = Objects.requireNonNull(apiClient, "apiClient cannot be null");
        logger.info("StoreService initialized successfully");
    }

    public StoreDTO createStore(UUID companyId, CreateStoreRequest request) throws CheqiSDKException {
        return createStore(companyId, request, RequestOptions.defaults());
    }

    /**
     * Creates a new store/location for a company.
     *
     * @param companyId The company ID to create the store for
     * @param request Store creation request with store details
     * @param options per-call options, such as the OAuth access token (needs WRITE_STORES scope)
     * @return Created Store with ID and timestamps
     * @throws CheqiSDKException if creation fails
     */
    public StoreDTO createStore(UUID companyId, CreateStoreRequest request, RequestOptions options) throws CheqiSDKException {
        validateCompanyId(companyId);

        if (request == null) {
            throw new CheqiSDKException("Store request cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        logger.info("Creating store '{}' for company {}", request.getStoreName(), companyId);

        try {
            StoreDTO store = apiClient.createStore(companyId, request, options);
            logger.info("Store created successfully: storeId={}", store.getId());
            return store;
        } catch (Exception e) {
            logger.error("Failed to create store: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to create store: " + e.getMessage(), e);
        }
    }

    public List<StoreDTO> getStores(UUID companyId) throws CheqiSDKException {
        return getStores(companyId, RequestOptions.defaults());
    }

    /**
     * Gets all stores for a company.
     *
     * @param companyId The company ID
     * @param options per-call options, such as the OAuth access token
     * @return List of stores
     * @throws CheqiSDKException if retrieval fails
     */
    public List<StoreDTO> getStores(UUID companyId, RequestOptions options) throws CheqiSDKException {
        validateCompanyId(companyId);

        logger.debug("Getting stores for company {}", companyId);

        try {
            return apiClient.getStores(companyId, null, options);
        } catch (Exception e) {
            logger.error("Failed to get stores: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to get stores: " + e.getMessage(), e);
        }
    }

    public List<StoreDTO> getActiveStores(UUID companyId) throws CheqiSDKException {
        return getActiveStores(companyId, RequestOptions.defaults());
    }

    /**
     * Gets active stores for a company.
     *
     * @param companyId The company ID
     * @param options per-call options, such as the OAuth access token
     * @return List of active stores
     * @throws CheqiSDKException if retrieval fails
     */
    public List<StoreDTO> getActiveStores(UUID companyId, RequestOptions options) throws CheqiSDKException {
        validateCompanyId(companyId);

        logger.debug("Getting active stores for company {}", companyId);

        try {
            return apiClient.getStores(companyId, true, options);
        } catch (Exception e) {
            logger.error("Failed to get active stores: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to get active stores: " + e.getMessage(), e);
        }
    }

    public StoreDTO getStore(UUID companyId, UUID storeId) throws CheqiSDKException {
        return getStore(companyId, storeId, RequestOptions.defaults());
    }

    /**
     * Gets a specific store by ID.
     *
     * @param companyId The company ID
     * @param storeId The store ID
     * @param options per-call options, such as the OAuth access token
     * @return Store details
     * @throws CheqiSDKException if retrieval fails
     */
    public StoreDTO getStore(UUID companyId, UUID storeId, RequestOptions options) throws CheqiSDKException {
        validateCompanyId(companyId);
        validateStoreId(storeId);

        logger.debug("Getting store {} for company {}", storeId, companyId);

        try {
            return apiClient.getStore(companyId, storeId, options);
        } catch (Exception e) {
            logger.error("Failed to get store: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to get store: " + e.getMessage(), e);
        }
    }

    public StoreDTO updateStore(UUID companyId, UUID storeId, CreateStoreRequest request) throws CheqiSDKException {
        return updateStore(companyId, storeId, request, RequestOptions.defaults());
    }

    /**
     * Updates a store.
     *
     * @param companyId The company ID
     * @param storeId The store ID to update
     * @param request Updated store details
     * @param options per-call options, such as the OAuth access token (needs WRITE_STORES scope)
     * @return Updated store
     * @throws CheqiSDKException if update fails
     */
    public StoreDTO updateStore(UUID companyId, UUID storeId, CreateStoreRequest request, RequestOptions options) throws CheqiSDKException {
        validateCompanyId(companyId);
        validateStoreId(storeId);

        if (request == null) {
            throw new CheqiSDKException("Store request cannot be null",
                    CheqiSDKException.ErrorCodes.VALIDATION_ERROR, 400, null);
        }

        logger.info("Updating store {} for company {}", storeId, companyId);

        try {
            StoreDTO store = apiClient.updateStore(companyId, storeId, request, options);
            logger.info("Store updated successfully: storeId={}", store.getId());
            return store;
        } catch (Exception e) {
            logger.error("Failed to update store: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to update store: " + e.getMessage(), e);
        }
    }

    public void deleteStore(UUID companyId, UUID storeId) throws CheqiSDKException {
        deleteStore(companyId, storeId, RequestOptions.defaults());
    }

    /**
     * Deletes a store.
     *
     * @param companyId The company ID
     * @param storeId The store ID to delete
     * @param options per-call options, such as the OAuth access token (needs WRITE_STORES scope)
     * @throws CheqiSDKException if deletion fails
     */
    public void deleteStore(UUID companyId, UUID storeId, RequestOptions options) throws CheqiSDKException {
        validateCompanyId(companyId);
        validateStoreId(storeId);

        logger.info("Deleting store {} for company {}", storeId, companyId);

        try {
            apiClient.deleteStore(companyId, storeId, options);
            logger.info("Store deleted successfully: storeId={}", storeId);
        } catch (Exception e) {
            logger.error("Failed to delete store: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to delete store: " + e.getMessage(), e);
        }
    }

    public void activateStore(UUID companyId, UUID storeId) throws CheqiSDKException {
        activateStore(companyId, storeId, RequestOptions.defaults());
    }

    /**
     * Activates a store.
     *
     * @param companyId The company ID
     * @param storeId The store ID to activate
     * @param options per-call options, such as the OAuth access token (needs WRITE_STORES scope)
     * @throws CheqiSDKException if activation fails
     */
    public void activateStore(UUID companyId, UUID storeId, RequestOptions options) throws CheqiSDKException {
        validateCompanyId(companyId);
        validateStoreId(storeId);

        logger.info("Activating store {} for company {}", storeId, companyId);

        try {
            apiClient.activateStore(companyId, storeId, options);
            logger.info("Store activated successfully: storeId={}", storeId);
        } catch (Exception e) {
            logger.error("Failed to activate store: {}", e.getMessage(), e);
            throw new CheqiSDKException("Failed to activate store: " + e.getMessage(), e);
        }
    }

    public void deactivateStore(UUID companyId, UUID storeId) throws CheqiSDKException {
        deactivateStore(companyId, storeId, RequestOptions.defaults());
    }

    /**
     * Deactivates a store.
     *
     * @param companyId The company ID
     * @param storeId The store ID to deactivate
     * @param options per-call options, such as the OAuth access token (needs WRITE_STORES scope)
     * @throws CheqiSDKException if deactivation fails
     */
    public void deactivateStore(UUID companyId, UUID storeId, RequestOptions options) throws CheqiSDKException {
        validateCompanyId(companyId);
        validateStoreId(storeId);

        logger.info("Deactivating store {} for company {}", storeId, companyId);

        try {
            apiClient.deactivateStore(companyId, storeId, options);
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

}
