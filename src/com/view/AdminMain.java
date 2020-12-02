package com.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class AdminMain implements Initializable
{
    @FXML
    private ScrollPane sp_admin_main;
    
    @FXML
    private Menu m_theater;
    
    @FXML
    private MenuItem mi_movie_add;
    
    @FXML
    private MenuItem mi_movie_change;
    
    @FXML
    private MenuItem mi_rsv_add;
    
    @FXML
    private MenuItem mi_rsv_manage;
    
    @FXML
    private Menu m_admin_account;
    
    @FXML
    private BorderPane bp_admin_sub;
    
    @FXML
    private ImageView img_web;
    
    // 초기화
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // 스크롤 속도 조정
        final double SPEED = 0.005;
        sp_admin_main.getContent().setOnScroll(scrollEvent ->
        {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            sp_admin_main.setVvalue(sp_admin_main.getVvalue() - deltaY);
        });
    }
    
    @FXML
    void menuAdminAccount(ActionEvent event)
    {
        loadPage("account_manage");
    }
    
    @FXML
    void menuPriceChange(ActionEvent event)
    {
        loadPage("price_change");
    }
    
    @FXML
    void menuMovieAdd(ActionEvent event)
    {
        loadPage("movie_add");
    }
    
    @FXML
    void menuMovieChange(ActionEvent event)
    {
        loadPage("movie_manage");
    }
    
    @FXML
    void menuRsvAdd(ActionEvent event)
    {
        loadPage("reservation_add");
    }
    
    @FXML
    void menuRsvManage(ActionEvent event)
    {
        loadPage("reservation_manage");
    }
    
    @FXML
    void menuTheaterManage(ActionEvent event)
    {
        loadPage("theater_manage");
    }
    
    @FXML
    void menuTimeTableManage(ActionEvent event)
    {
        loadPage("movie_table_manage");
    }
    
    @FXML
    void menuSatistics(ActionEvent event)
    {
        loadPage("statistics_information");
    }
    
    // 각 파일이름에 해당하는 뷰 불러오기
    private void loadPage(String file_name)
    {
        try
        {
            Parent root = FXMLLoader.load(AdminMain.class.getResource("./xml/admin_sub_page/" + file_name + ".fxml"));
            bp_admin_sub.setCenter(root);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
