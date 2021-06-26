package org.udg.pds.todoandroid.rest;

import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Product;
import org.udg.pds.todoandroid.entity.Rate;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserLogin;
import org.udg.pds.todoandroid.entity.UserRegister;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

import java.util.List;

/**
 * Created by imartin on 13/02/17.
 */
public interface TodoApi {
    @POST("/users/login")
    Call<User> login(@Body UserLogin login);

    @GET("/users/check")
    Call<String> check();

    @GET("/users/me")
    Call<User> getMe();

    @GET("/users/{id}")
    Call<User> getUser(@Path("id") Long id);

    @POST("/users/logout")
    Call<String> logout();

    @POST("/products")
    Call<IdObject> addProduct(@Body Product product);

    @GET("/products/{id}")
    Call<Product> getProduct(@Path("id") Long idProduct);

    @POST("/users/register")
    Call<String> register(@Body UserRegister register);

    @GET("/products")
    Call<List<Product>> getAllProducts();

    @GET("/products/search/{products}")
    Call<List<Product>> searchProduct(@Path("products") String products);

    @GET("/users/me/products")
    Call<List<Product>> getOnSaleProducts();

    @POST("/users/me/favorited/{id}")
    Call<String> addProductToWishlist(@Path("id") Long productId);

    @DELETE("/users/me/favorited/{id}")
    Call<String> deleteProductFromWishList(@Path("id") Long productId);

    @GET("/users/me/favorited")
    Call<List<Product>> getUserFavoriteProducts();

    @GET("/users/me/favorited/{id}")
    Call<Product> checkIfProductExistsOnWishList(@Path("id") Long productId);

    @PATCH("/users/me")
    Call<String> patchSelfUserData(@Body User user);

    @PATCH("/products/{id}")
    Call<String> patchProdData(@Path("id") Long idProduct, @Body Product product);

    @DELETE("/products/{id}")
    Call<String> deleteProduct(@Path("id") Long idProduct);

    @DELETE("/users/me")
    Call<String> deleteSelfUser();

    @POST("/images")
    @Multipart
    Call<String> uploadImage(@Part MultipartBody.Part file);

    @GET("/users/{id}/rates")
    Call<List<Rate>> getRates(@Path("id") Long idProduct);

    @POST("/users/{id}/rates")
    Call<String> addRate(@Path("id") Long idUserRated, @Body Rate rate);

    @POST("/users/me/token/{token}")
    Call<String> refreshToken(@Path("token") String token);

    @POST("/send/buyRequest/{idDest}")
    Call<String> sendBuyRequestMessage(@Path("idDest") Long idUserDesti);

}

