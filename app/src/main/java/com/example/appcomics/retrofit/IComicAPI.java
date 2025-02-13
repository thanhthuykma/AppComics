package com.example.appcomics.retrofit;

import com.example.appcomics.Model.Author;
import com.example.appcomics.Model.Banner;
import com.example.appcomics.Model.Catergory;
import com.example.appcomics.Model.Chapter;
import com.example.appcomics.Model.ChapterCountResponse;
import com.example.appcomics.Model.CategoryResponse;
import com.example.appcomics.Model.Comic;
import com.example.appcomics.Model.ComicCountResponse;
import com.example.appcomics.Model.Comment;
import com.example.appcomics.Model.Download;
import com.example.appcomics.Model.Favourite;
import com.example.appcomics.Model.ForgotPasswordRequest;
import com.example.appcomics.Model.History;
import com.example.appcomics.Model.LinkResponse;
import com.example.appcomics.Model.Links;
import com.example.appcomics.Model.LoginResponse;
import com.example.appcomics.Model.RefreshTokenResponse;
import com.example.appcomics.Model.RegisterResponse;
import com.example.appcomics.Model.ThongTin;
import com.example.appcomics.Model.VerifyCodeRequest;
import com.example.appcomics.Model.ViewsResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IComicAPI {
    @GET("banner")
    Call<List<Banner>> getBannerList();
    @GET("comics")
    Call<List<Comic>> getComicList();
    @GET("/manga/{mangaid}/chapters")
    Call<List<Chapter>> getChaptersByMangaId(@Path("mangaid") int mangaId);
    @GET("/link/{chapterid}/links")
    Call<List<Links>> getLinks(@Path("chapterid") int chaterId);
    @PUT("/manga/view/{id}")
    Call<Void> updateViews(@Path("id") int mangaid);
    //Thêm truyện yêu thich
    @PUT("/favourite")
    Call<Void> insertFavourites(@Body Favourite favourites);
    @POST("/login")
    @FormUrlEncoded
    Call<LoginResponse> login(@Field("username") String username, @Field("password") String password);
    //Đăng ký
    @POST("/register")
    @FormUrlEncoded
    Call<RegisterResponse> register(@Field("username") String username,@Field("email") String email, @Field("password") String password, @Field("repassword") String repass);
    //Xóa khỏ yeuthich
    @DELETE("/deletefav/{mangaid}")
    Call<Void> deleteFav(@Path("mangaid") int mangaid);
    //lấy dữ liệu truyện yêu thích
    @GET("/api/favourites/{username}")
    Call<List<Favourite>> getFavourites(@Path("username") String username);
    //Thêm vào bảng lịch sử
    @PUT("/history")
    Call<Void> insertHistory(@Body History history);
    //Thêm vào bảng download
    @PUT("/download")
    Call<Void> insertDownload(@Body Download download);
    //Đếm số chương
    @GET("/chapsize/{mangaid}")
    Call<List<ChapterCountResponse>> getChapsize(@Path("mangaid") int mangaid);
    //Đếm số truyện
    @GET("/comicsize")
    Call<List<ComicCountResponse>> getComicsize();
    //Lấy dữ liệu lịch sử xem
    @GET("/history/{username}")
    Call<List<History>> getHistory(@Path("username") String username);
    //Lấy danh sách download
    @GET("/downloadhis/{username}")
    Call<List<History>> getDownload(@Path("username") String username);
    //Lấy views
    @GET("/views/{id}")
    Call<List<ViewsResponse>> getViews(@Path("id") int id);
    //Lấy các thể loại
    @GET("/categories")
    Call<List<Catergory>> getCategories();
    //Lọc truyện dựa vào thể loại
    @POST("/filter")
    @FormUrlEncoded
    Call<List<Comic>> filterComics(@Field("data") String data);
    //Lấy truyện bằng id
    @GET("/comics/{id}")
    Call<List<Comic>> getComicsbyId(@Path("id") int id);
    //Lấy link tải truyện
    @GET("/download/{mangaid}")
    Call<List<LinkResponse>> getLinkDownload(@Path("mangaid") int mangaid);

    //tìm kiếm truyện
    @GET("/search")
    Call<List<Comic>> searchComic(@Query("keyword") String keyword);

    @POST("/forgot-password")
    Call<ResponseBody> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("/verify-code")
    Call<ResponseBody> verifyCode(@Body VerifyCodeRequest request);
    // API lấy tất cả bình luận của truyện theo comic_id
    @GET("/comments/{comic_id}")
    Call<List<Comment>> getCommentsByComicId(@Path("comic_id") int comicId);
    // API thêm một bình luận mới
    @POST("/comments")
    Call<Comment> addComment(@Body Comment comment);
    // API thích một bình luận
    @PATCH("/comments/{id}/like")
    Call<Void> likeComment(@Path("id") int commentId);

    // API thêm câu trả lời cho bình luận
    @GET("/comments/{manga_id}")
    Call<List<Comment>> getComments(@Path("manga_id") int mangaId);
    //Lấy tên tác giả
    @GET("/tacgia/{mangaid}")
    Call<Author> tacgia(@Path("mangaid") int mangaid);
    //Lấy thông tin truyện
    @GET("/thongtin/{mangaid}")
    Call<ThongTin> thongtin(@Path("mangaid") int mangaid);
    //Lấy thể loại
    @GET("/theloai/{mangaid}")
    Call<CategoryResponse> getTheLoai(@Path("mangaid") int mangaid);

    //Kiểm tra username
    @POST("/user")
    @FormUrlEncoded
    Call<LoginResponse> usernameCheck(@Field("username") String username);
    //refresh token
        @POST("/refresh-token")
        Call<RefreshTokenResponse> refreshAccessToken(@Body String refreshToken);

}
