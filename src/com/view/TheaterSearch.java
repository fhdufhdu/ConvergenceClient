package com.view;

import com.db.model.TheaterDTO;
import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class TheaterSearch
{
	@FXML
	private Text t_theater_name;
	
	@FXML
	private Text t_screen;
	
	@FXML
	private Text t_total_seat;
	
	@FXML
	private Text t_address;
	
	@FXML
	private WebView map;
	
	@FXML
	private Text t_type_1;
	
	@FXML
	private Text t_type_2;
	
	@FXML
	private Text t_type_3;
	
	// 메인에서 영화관 상세 페이지 선택시 실행될 초기화 함수
	public void initData(TheaterDTO theater)
	{
		try
		{
			WebEngine webEngine = map.getEngine();
			// 웹뷰 로딩 완료시 실행할 js(지도 api)
			map.getEngine().getLoadWorker().stateProperty().addListener((obs, old, neww) ->
			{
				if (neww == Worker.State.SUCCEEDED)
				{
					webEngine.executeScript("loadMap('" + theater.getAddress() + "');");
				}
			});
			webEngine.load(TheaterSearch.class.getResource("./xml/user_sub_page/naver_map.html").toExternalForm());
			
			t_theater_name.setText(theater.getName());
			t_screen.setText(theater.getTotalScreen() + " 개관");
			t_total_seat.setText(theater.getTotalSeats() + "석");
			t_address.setText("○ " + theater.getAddress());
			
			// 가격정보 요청
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_PRICE_VIEW);
			
			String packet = mainGUI.readLine(); // 요청 응답 수신
			String packetArr[] = packet.split("`"); // 패킷 분할
			String packetType = packetArr[0];
			String packetCode = packetArr[1];
			
			if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_PRICE_VIEW))
			{
				String result = packetArr[2]; // 요청 결과
				switch (result)
				{
					case "1": // 요청 성공 시 값 세팅
						String priceArr[] = packetArr[3].split("\\{"); // 조조, 일반, 심야별로 정보 분할
						for (String priceInfo : priceArr)
						{
							String priceList[] = priceInfo.split("\\|"); // 내용과 금액으로 정보 분할
							String priceType = priceList[0];
							String price = priceList[1];
							switch (priceType)
							{
								case "1": // 조조 금액
								{
									t_type_1.setText(price);
									break;
								}
								case "2": // 일반 금액
								{
									t_type_2.setText(price);
									break;
								}
								case "3": // 심야  금액
								{
									t_type_3.setText(price);
									break;
								}
							}
						}
						break;
					case "2": // 요청 실패
						mainGUI.alert("경고", "가격정보 요청에 실패하였습니다.");
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
