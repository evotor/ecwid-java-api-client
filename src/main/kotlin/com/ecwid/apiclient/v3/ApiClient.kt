package com.ecwid.apiclient.v3

import com.ecwid.apiclient.v3.config.ApiServerDomain
import com.ecwid.apiclient.v3.config.ApiStoreCredentials
import com.ecwid.apiclient.v3.config.LoggingSettings
import com.ecwid.apiclient.v3.dto.batch.request.CreateBatchRequest
import com.ecwid.apiclient.v3.dto.batch.request.CreateBatchRequestWithIds
import com.ecwid.apiclient.v3.dto.batch.request.GetEscapedBatchRequest
import com.ecwid.apiclient.v3.dto.batch.result.CreateBatchResult
import com.ecwid.apiclient.v3.dto.batch.result.GetEscapedBatchResult
import com.ecwid.apiclient.v3.dto.batch.result.GetTypedBatchResult
import com.ecwid.apiclient.v3.dto.cart.request.*
import com.ecwid.apiclient.v3.dto.cart.result.*
import com.ecwid.apiclient.v3.dto.category.request.*
import com.ecwid.apiclient.v3.dto.category.result.*
import com.ecwid.apiclient.v3.dto.coupon.request.*
import com.ecwid.apiclient.v3.dto.coupon.result.*
import com.ecwid.apiclient.v3.dto.customer.request.*
import com.ecwid.apiclient.v3.dto.customer.result.*
import com.ecwid.apiclient.v3.dto.customergroup.request.*
import com.ecwid.apiclient.v3.dto.customergroup.result.*
import com.ecwid.apiclient.v3.dto.order.request.*
import com.ecwid.apiclient.v3.dto.order.result.*
import com.ecwid.apiclient.v3.dto.product.request.*
import com.ecwid.apiclient.v3.dto.product.result.*
import com.ecwid.apiclient.v3.dto.producttype.request.*
import com.ecwid.apiclient.v3.dto.producttype.result.*
import com.ecwid.apiclient.v3.dto.profile.request.StoreProfileRequest
import com.ecwid.apiclient.v3.dto.profile.request.StoreProfileUpdateRequest
import com.ecwid.apiclient.v3.dto.profile.result.FetchedStoreProfile
import com.ecwid.apiclient.v3.dto.profile.result.StoreProfileUpdateResult
import com.ecwid.apiclient.v3.dto.variation.request.*
import com.ecwid.apiclient.v3.dto.variation.result.*
import com.ecwid.apiclient.v3.httptransport.HttpTransport
import com.ecwid.apiclient.v3.impl.*
import com.ecwid.apiclient.v3.jsontransformer.JsonTransformerProvider
import com.ecwid.apiclient.v3.jsontransformer.PolymorphicType

class ApiClient private constructor(
		storeProfileApiClient: StoreProfileApiClient,
		productsApiClient: ProductsApiClient,
		categoriesApiClient: CategoriesApiClient,
		ordersApiClient: OrdersApiClient,
		productTypesApiClient: ProductTypesApiClient,
		customersApiClient: CustomersApiClient,
		customerGroupsApiClient: CustomerGroupsApiClient,
		productVariationsApiClient: ProductVariationsApiClient,
		batchApiClient: BatchApiClient,
		discountCouponsApiClient: CouponsApiClient,
		cartsApiClient: CartsApiClient
) :
		StoreProfileApiClient by storeProfileApiClient,
		ProductsApiClient by productsApiClient,
		CategoriesApiClient by categoriesApiClient,
		OrdersApiClient by ordersApiClient,
		ProductTypesApiClient by productTypesApiClient,
		CustomersApiClient by customersApiClient,
		CustomerGroupsApiClient by customerGroupsApiClient,
		ProductVariationsApiClient by productVariationsApiClient,
		BatchApiClient by batchApiClient,
		CouponsApiClient by discountCouponsApiClient,
		CartsApiClient by cartsApiClient {

	companion object {
		fun create(apiServerDomain: ApiServerDomain,
				   storeCredentials: ApiStoreCredentials,
				   loggingSettings: LoggingSettings = LoggingSettings(),
				   httpTransport: HttpTransport,
				   jsonTransformerProvider: JsonTransformerProvider): ApiClient {

			val productOptionsPolymorphicType = PolymorphicType(
					rootClass = FetchedProduct.ProductOption::class.java,
					jsonFieldName = "type",
					childClasses = mapOf(
							"select" to FetchedProduct.ProductOption.SelectOption::class.java,
							"size" to FetchedProduct.ProductOption.SizeOption::class.java,
							"radio" to FetchedProduct.ProductOption.RadioOption::class.java,
							"checkbox" to FetchedProduct.ProductOption.CheckboxOption::class.java,
							"textfield" to FetchedProduct.ProductOption.TextFieldOption::class.java,
							"textarea" to FetchedProduct.ProductOption.TextAreaOption::class.java,
							"date" to FetchedProduct.ProductOption.DateOption::class.java,
							"files" to FetchedProduct.ProductOption.FilesOption::class.java
					)
			)

			val polymorphicTypes = listOf(productOptionsPolymorphicType)
			val jsonTransformer = jsonTransformerProvider.build(polymorphicTypes)
			val apiClientHelper = ApiClientHelper(apiServerDomain, storeCredentials, loggingSettings, httpTransport, jsonTransformer)

			return ApiClient(
					StoreProfileApiClientImpl(apiClientHelper),
					ProductsApiClientImpl(apiClientHelper),
					CategoriesApiClientImpl(apiClientHelper),
					OrdersApiClientImpl(apiClientHelper),
					ProductTypesApiClientImpl(apiClientHelper),
					CustomersApiClientImpl(apiClientHelper),
					CustomerGroupsApiClientImpl(apiClientHelper),
					ProductVariationsApiClientImpl(apiClientHelper),
					BatchApiClientImpl(apiClientHelper),
					CouponsApiClientImpl(apiClientHelper),
					CartsApiClientImpl(apiClientHelper)
			)

		}
	}
}

// Store-Profile
// https://api-docs.ecwid.com/reference/store-profile
interface StoreProfileApiClient {
	fun getStoreProfile(request: StoreProfileRequest): FetchedStoreProfile
	fun updateStoreProfile(request: StoreProfileUpdateRequest): StoreProfileUpdateResult
//	fun getShippingOptions()
//	fun addShippingOption()
//	fun updateShippingOption()
//	fun getPaymentOptions()
//	fun addPaymentOption()
//	fun updatePaymentOption()
//	fun updateStoreLogo()
//	fun removeStoreLogo()
//	fun updateInvoiceLogo()
//	fun removeInvoiceLogo()
//	fun updateEmailLogo()
//	fun removeEmailLogo()
}

// Products
// https://developers.ecwid.com/api-documentation/products
interface ProductsApiClient {
	fun searchProducts(request: ProductsSearchRequest.ByFilters): ProductsSearchResult
	fun searchProducts(request: ProductsSearchRequest.ByIds): ProductsSearchResult
	fun searchProductsAsSequence(request: ProductsSearchRequest.ByFilters): Sequence<FetchedProduct>
	fun searchProductsAsSequence(request: ProductsSearchRequest.ByIds): Sequence<FetchedProduct>
	fun getProductDetails(request: ProductDetailsRequest): FetchedProduct
	fun createProduct(request: ProductCreateRequest): ProductCreateResult
	fun updateProduct(request: ProductUpdateRequest): ProductUpdateResult
	fun updateProductInventory(request: ProductInventoryUpdateRequest): ProductInventoryUpdateResult
	fun deleteProduct(request: ProductDeleteRequest): ProductDeleteResult
	fun uploadProductImage(request: ProductImageUploadRequest): ProductImageUploadResult
	fun deleteProductImage(request: ProductImageDeleteRequest): ProductImageDeleteResult
	fun uploadProductGalleryImage(request: ProductGalleryImageUploadRequest): ProductGalleryImageUploadResult
	fun deleteProductGalleryImage(request: ProductGalleryImageDeleteRequest): ProductGalleryImageDeleteResult
	fun deleteProductGalleryImages(request: ProductGalleryImagesDeleteRequest): ProductGalleryImagesDeleteResult
	fun downloadProductFile(request: ProductFileDownloadRequest): ByteArray
	fun uploadProductFile(request: ProductFileUploadRequest): ProductFileUploadResult
	fun updateProductFile(request: ProductFileUpdateRequest): ProductFileUpdateResult
	fun deleteProductFile(request: ProductFileDeleteRequest): ProductFileDeleteResult
	fun deleteProductFiles(request: ProductFilesDeleteRequest): ProductFilesDeleteResult
	fun searchDeletedProducts(request: DeletedProductsSearchRequest): DeletedProductsSearchResult
	fun searchDeletedProductsAsSequence(request: DeletedProductsSearchRequest): Sequence<DeletedProduct>
}

// Categories
// https://developers.ecwid.com/api-documentation/categories
interface CategoriesApiClient {
	fun searchCategories(request: CategoriesSearchRequest): CategoriesSearchResult
	fun searchCategoriesAsSequence(request: CategoriesSearchRequest): Sequence<FetchedCategory>
	fun getCategoryDetails(request: CategoryDetailsRequest): FetchedCategory
	fun createCategory(request: CategoryCreateRequest): CategoryCreateResult
	fun updateCategory(request: CategoryUpdateRequest): CategoryUpdateResult
	fun deleteCategory(request: CategoryDeleteRequest): CategoryDeleteResult
	fun uploadCategoryImage(request: CategoryImageUploadRequest): CategoryImageUploadResult
	fun deleteCategoryImage(request: CategoryImageDeleteRequest): CategoryImageDeleteResult
}

// Orders
// https://developers.ecwid.com/api-documentation/orders
interface OrdersApiClient {
	fun searchOrders(request: OrdersSearchRequest): OrdersSearchResult
	fun searchOrdersAsSequence(request: OrdersSearchRequest): Sequence<FetchedOrder>
	fun getOrderDetails(request: OrderDetailsRequest): FetchedOrder
	fun getOrderInvoice(request: OrderInvoiceRequest): String
	fun createOrder(request: OrderCreateRequest): OrderCreateResult
	fun updateOrder(request: OrderUpdateRequest): OrderUpdateResult
	fun deleteOrder(request: OrderDeleteRequest): OrderDeleteResult
	fun uploadOrderItemOptionFile(request: OrderItemOptionFileUploadRequest): OrderItemOptionFileUploadResult
	fun deleteOrderItemOptionFile(request: OrderItemOptionFileDeleteRequest): OrderItemOptionFileDeleteResult
	fun deleteOrderItemOptionFiles(request: OrderItemOptionFilesDeleteRequest): OrderItemOptionFileDeleteResult
	fun searchDeletedOrders(request: DeletedOrdersSearchRequest): DeletedOrdersSearchResult
	fun searchDeletedOrdersAsSequence(request: DeletedOrdersSearchRequest): Sequence<DeletedOrder>
}

// Product types
// https://developers.ecwid.com/api-documentation/product-types
interface ProductTypesApiClient {
	fun getAllProductTypes(request: ProductTypesGetAllRequest): ProductTypesGetAllResult
	fun getProductTypeDetails(request: ProductTypeDetailsRequest): FetchedProductType
	fun createProductType(request: ProductTypeCreateRequest): ProductTypeCreateResult
	fun updateProductType(request: ProductTypeUpdateRequest): ProductTypeUpdateResult
	fun deleteProductType(request: ProductTypeDeleteRequest): ProductTypeDeleteResult
}

// Customers
// https://developers.ecwid.com/api-documentation/customers
interface CustomersApiClient {
	fun searchCustomers(request: CustomersSearchRequest): CustomersSearchResult
	fun searchCustomersAsSequence(request: CustomersSearchRequest): Sequence<FetchedCustomer>
	fun getCustomerDetails(request: CustomerDetailsRequest): FetchedCustomer
	fun createCustomer(request: CustomerCreateRequest): CustomerCreateResult
	fun updateCustomer(request: CustomerUpdateRequest): CustomerUpdateResult
	fun deleteCustomer(request: CustomerDeleteRequest): CustomerDeleteResult
	fun searchDeletedCustomers(request: DeletedCustomersSearchRequest): DeletedCustomersSearchResult
	fun searchDeletedCustomersAsSequence(request: DeletedCustomersSearchRequest): Sequence<DeletedCustomer>
}

// Customer groups
// https://developers.ecwid.com/api-documentation/customer-groups
interface CustomerGroupsApiClient {
	fun searchCustomerGroups(request: CustomerGroupsSearchRequest): CustomerGroupsSearchResult
	fun searchCustomerGroupsAsSequence(request: CustomerGroupsSearchRequest): Sequence<FetchedCustomerGroup>
	fun getCustomerGroupDetails(request: CustomerGroupDetailsRequest): FetchedCustomerGroup
	fun createCustomerGroup(request: CustomerGroupCreateRequest): CustomerGroupCreateResult
	fun updateCustomerGroup(request: CustomerGroupUpdateRequest): CustomerGroupUpdateResult
	fun deleteCustomerGroup(request: CustomerGroupDeleteRequest): CustomerGroupDeleteResult
}

// Batch requests
// https://developers.ecwid.com/api-documentation/batch-requests
interface BatchApiClient {
	fun createBatch(request: CreateBatchRequestWithIds): CreateBatchResult
	fun createBatch(request: CreateBatchRequest): CreateBatchResult
	fun getTypedBatch(request: GetEscapedBatchRequest): GetTypedBatchResult
	fun getEscapedBatch(request: GetEscapedBatchRequest): GetEscapedBatchResult
}

// Store information
// https://developers.ecwid.com/api-documentation/store-information
// TODO

// Product variations
// https://developers.ecwid.com/api-documentation/product-variations
// TODO
interface ProductVariationsApiClient {
	fun createProductVariation(request: CreateProductVariationRequest): CreateProductVariationResult
	fun uploadVariationImage(request: ProductVariationImageUploadRequest): ProductVariationImageUploadResult
	fun getAllProductVariations(request: ProductVariationsRequest): ProductVariationsResult
	fun getProductVariation(request: ProductVariationDetailsRequest): FetchedVariation
	fun updateProductVariation(request: UpdateProductVariationRequest): UpdateProductVariationResult
	fun deleteProductVariation(request: DeleteProductVariationRequest): DeleteProductVariationsResult
	fun deleteAllProductVariations(request: DeleteAllProductVariationsRequest): DeleteProductVariationsResult
	fun adjustVariationInventory(request: AdjustVariationInventoryRequest): AdjustVariationInventoryResult
	fun deleteVariationImage(request: ProductVariationImageDeleteRequest): ProductVariationImageDeleteResult
}

// Carts
// https://developers.ecwid.com/api-documentation/carts
interface CartsApiClient {
	fun searchCarts(request: CartsSearchRequest): CartsSearchResult
	fun searchCartsAsSequence(request: CartsSearchRequest): List<FetchedCart>
	fun getCartDetails(request: CartDetailsRequest): FetchedCart
	fun updateCart(request: CartUpdateRequest): CartUpdateResult
	fun calculateOrderDetails(request: CalculateOrderDetailsRequest): CalculateOrderDetailsResult
	fun convertCartToOrder(request: ConvertCartToOrderRequest): ConvertCartToOrderResult
}

// Discount coupons
// https://developers.ecwid.com/api-documentation/discount-coupons
interface CouponsApiClient {
	fun searchCoupons(request: CouponSearchRequest): CouponSearchResult
	fun searchCouponsAsSequence(request: CouponSearchRequest): Sequence<FetchedCoupon>
	fun getCouponDetails(request: CouponDetailsRequest): FetchedCoupon
	fun createCoupon(request: CouponCreateRequest): CouponCreateResult
	fun updateCoupon(request: CouponUpdateRequest): CouponUpdateResult
	fun deleteCoupon(request: CouponDeleteRequest): CouponDeleteResult
}

// Application
// https://developers.ecwid.com/api-documentation/application
// TODO

// Starter site
// https://developers.ecwid.com/api-documentation/starter-site
// TODO

// Static store pages
// https://developers.ecwid.com/api-documentation/static-store-pages
// TODO

