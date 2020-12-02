package com.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.db.model.TheaterDTO;
import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

public class UserMain implements Initializable
{
	private ArrayList<TheaterDTO> theater_list;
	public static BorderPane user_sub_root; // 다른 컨트롤러에서도 화면 전환가능하게하기 위해
	
	@FXML
	private MenuButton mb_theater;
	
	@FXML
	private BorderPane bp_user_sub;
	
	@FXML
	private ScrollPane sp_user_main;
	
	// 유저 메인 초기화
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		try
		{
			// 스크롤 속도 조절
			final double SPEED = 0.005;
			sp_user_main.getContent().setOnScroll(scrollEvent ->
			{
				double deltaY = scrollEvent.getDeltaY() * SPEED;
				sp_user_main.setVvalue(sp_user_main.getVvalue() - deltaY);
			});
			
			user_sub_root = bp_user_sub;
			theater_list = new ArrayList<TheaterDTO>();
			
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_THEATER_VIEW);
			
			String packet = mainGUI.readLine();
			String packetArr[] = packet.split("`");
			String packetType = packetArr[0];
			String packetCode = packetArr[1];
			
			if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_THEATER_VIEW))
			{
				String result = packetArr[2];
				switch (result)
				{
					case "1":
						String theaterList[] = packetArr[3].split("\\{");
						int i = 0; // 메뉴 id 번호
						for (String theater : theaterList)
						{
							String theaterArr[] = theater.split("\\|");
							String id = theaterArr[0];
							String name = theaterArr[1];
							String address = theaterArr[2];
							int total_screen = Integer.parseInt(theaterArr[3]);
							int total_seats = Integer.parseInt(theaterArr[4]);
							theater_list.add(new TheaterDTO(id, name, address, total_screen, total_seats));
							
							// 영화관 선택 메뉴 생성
							MenuItem theater_name = new MenuItem(name);
							theater_name.setId(Integer.toString(i));
							theater_name.setOnAction(new EventHandler<ActionEvent>() // 영화관 선택시 발생 이벤트
							{
								public void handle(ActionEvent event)
								{
									try
									{
										// 해당 영화관 상세정보 페이지로 이동
										FXMLLoader loader = new FXMLLoader(UserMain.class.getResource("./xml/user_sub_page/theater_search.fxml"));
										Parent root = (Parent) loader.load();
										TheaterSearch controller = loader.<TheaterSearch>getController();
										controller.initData(theater_list.get(Integer.valueOf(theater_name.getId())));
										
										bp_user_sub.setCenter(root);
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}
								}
							});
							mb_theater.getItems().add(theater_name);
							i++;
						}
						
						break;
					case "2":
						mainGUI.alert("경고", "영화관 없음");
						break;
				}
			}
		}
		catch (Exception e)
		{
			mainGUI.alert("오류", "서버에서 DB서버 연결 오류");
			e.printStackTrace();
		}
	}
	
	@FXML
	void menuTimeTable(ActionEvent event)
	{
		loadPage("movie_table");
	}
	
	@FXML
	void currentMovie(ActionEvent event)
	{
		loadPage("movie_present");
	}
	
	@FXML
	void soonMovie(ActionEvent event)
	{
		loadPage("movie_soon");
	}
	
	@FXML
	void searchMovie(ActionEvent event)
	{
		loadPage("movie_search");
	}
	
	@FXML
	void cancelRsv(ActionEvent event)
	{
		loadPage("reservation_cancel");
	}
	
	// 페이지 로딩 함수
	private void loadPage(String file_name)
	{
		try
		{
			MovieDetail.stopWebview(); // 웹뷰(동영상플레이어) 실행 시 종료하게 함
			Parent root = FXMLLoader.load(UserMain.class.getResource("./xml/user_sub_page/" + file_name + ".fxml"));
			user_sub_root.setCenter(root);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
