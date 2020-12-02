package com.view;

import java.sql.Timestamp;

import com.db.model.MovieDTO;
import com.db.model.TimeTableDTO;
import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class MoviePresentSub
{
    private MovieDTO movie;
    
    @FXML
    private ImageView iv_movie_poster;
    
    @FXML
    private Text t_movie_title;
    
    @FXML
    private Text t_rsv_rate;
    
    @FXML
    void clickedTheater(ActionEvent event) // 각 sub페이지 클릭시 영화 상세 페이지(MovieDetail.java)로 이동
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(MovieTable.class.getResource("./xml/user_sub_page/movie_detail.fxml"));
            Parent root = loader.load();
            MovieDetail controller = loader.<MovieDetail>getController();
            controller.initData(movie);
            
            UserMain.user_sub_root.setCenter(root);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    // MoviePresent에서 값을 받아와서 이미지와 글자등 설정
    public void initData(MovieDTO movie)
    {
        try
        {
            this.movie = movie;
            Thread image_load = new Thread()
            {
                @Override
                public void run()
                {
                    Platform.runLater(() ->
                    {
                        Image image = new Image(movie.getPosterPath());
                        iv_movie_poster.setImage(image);
                    });
                    return;
                }
            };
            image_load.start();
            
            t_movie_title.setText(movie.getTitle());
            
            mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_MOVIESUB_VIEW + "`" + movie.getId());
            
            while (true)
            {
                String packet = mainGUI.readLine();
                System.out.println(packet);
                String packetArr[] = packet.split("`"); // 패킷 분할
                String packetType = packetArr[0];
                String packetCode = packetArr[1];
                
                if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_MOVIESUB_VIEW))
                {
                    String result = packetArr[2];
                    
                    switch (result)
                    {
                        case "1":
                        {
                            double rsv_rate = Double.parseDouble(packetArr[3]);
                            String aver_star = packetArr[4];
                            t_rsv_rate.setText("예매율 : " + String.format("%.2f", rsv_rate * 100));
                            t_rsv_rate.setText(t_rsv_rate.getText() + "% | 평점 : " + aver_star);
                            break;
                        }
                        case "2":
                        {
                            mainGUI.alert("오류", "예매율 요청에 실패했습니다.");
                            break;
                        }
                    }
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
}
