package com.view;

import com.db.model.MovieDTO;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

public class MovieSoonDetail
{
    private MovieDTO movie;
    
    @FXML
    private Button btn_reservation;
    
    @FXML
    private ImageView image_movie;
    
    @FXML
    private Text text_title;
    
    @FXML
    private Text text_open_date;
    
    @FXML
    private Text text_director;
    
    @FXML
    private Text text_actor;
    
    @FXML
    private ImageView image_stillcut;
    
    @FXML
    private ImageView image_stillcut2;
    
    @FXML
    private ImageView image_stillcut3;
    
    @FXML
    private Text text_plot;
    
    @FXML
    private WebView web_trailer;
    
    static private WebView webview;
    
    public void initData(MovieDTO movie)
    {
        try
        {
            this.movie = movie;
            webview = web_trailer; // 백그라운드로 돌아가는 웹뷰 종료를 위해서 값 복사
            
            // 스틸컷 url 분리
            String stillcut[] = movie.getStillCutPath().split(" ");
            ImageView view_arr[] = {
                    image_stillcut, image_stillcut2, image_stillcut3 };
            
            // 영화 예매 안해두면 입력 불가능하게 - 얘는 죽여도 될 것 같음
            
            text_title.setText(movie.getTitle());
            text_open_date.setText(movie.getReleaseDate().toString());
            text_director.setText(movie.getDirector());
            text_actor.setText(movie.getActor());
            text_plot.wrappingWidthProperty().set(225);
            text_plot.setText(movie.getPlot());
            Image image = new Image(movie.getPosterPath());
            image_movie.setImage(image);
            for (int i = 0; i < view_arr.length; i++)
            {
                Image image_temp = new Image(stillcut[i]);
                view_arr[i].setImage(image_temp);
            }
            String[] trailer = movie.getTrailerPath().split("v=");
            try
            {
                web_trailer.getEngine().load("http://www.youtube.com/embed/" + trailer[1] + "?autoplay=0");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    @FXML
    void getReservation(ActionEvent event)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(MovieTable.class.getResource("./xml/user_sub_page/movie_table.fxml"));
            Parent root = loader.load();
            MovieTable controller = loader.<MovieTable>getController();
            controller.initData(movie);
            
            UserMain.user_sub_root.setCenter(root);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    static public void stopWebview()
    {
        if (webview != null)
            webview.getEngine().load(null);
    }
    
}
