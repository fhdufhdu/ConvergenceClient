package com.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

public class Test implements Initializable
{
    
    @FXML
    private Text current_time;
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        /*
         * Date date = new Date(); SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         * 
         * //SimpleDateFormat 을 이용해 String 타입으로 가져오기
         * 
         * current_time.setText(dateFormat.format(date));
         */
    }
    
    void initData(String a)
    {
        current_time.setText(a);
    }
    
}
