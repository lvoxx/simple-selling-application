package com.shitcode.demo1.mapper;

import java.util.List;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shitcode.demo1.dto.PaymentDTO;
import com.shitcode.demo1.dto.PaymentDTO.PaypalTransactionResponse;
import com.shitcode.demo1.entity.PaypalTransaction;
import com.shitcode.demo1.entity.Recipe;
import com.shitcode.demo1.entity.RecipeProduct;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RecipeMapper {
    @Mapping(source = "id", target = "recipeId")
    @Mapping(source = "recipeProducts", target = "recipeProducts")
    @Mapping(source = "paypalTransaction", target = "paypalTransaction")
    PaymentDTO.Response toRecipeResponse(Recipe recipe);

    @Mapping(source = "categoryName", target = "categoryName")
    @Mapping(source = "productName", target = "productName")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "discountName", target = "discountName")
    @Mapping(source = "discountType", target = "discountType")
    @Mapping(source = "discountAmount", target = "discountAmount")
    @Mapping(source = "subTotal", target = "subTotal")
    PaymentDTO.ProductWithQuantityResponse toRecipeProductResponse(RecipeProduct recipeProduct);

    List<PaymentDTO.ProductWithQuantityResponse> toRecipeProductResponseList(List<RecipeProduct> recipeProducts);

    @Mapping(source = "transactionId", target = "transactionId")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "transactionDate", target = "transactionDate")
    PaypalTransactionResponse toPaypalTransactionResponse(PaypalTransaction paypalTransaction);
}
