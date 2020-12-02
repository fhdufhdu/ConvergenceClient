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
	
	public void initData(TheaterDTO theater)
	{
		try
		{
			WebEngine webEngine = map.getEngine();
			
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
			
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_PRICE_VIEW);
			
			String packet = mainGUI.readLine();
			String packetArr[] = packet.split("!");
			String packetType = packetArr[0];
			String packetCode = packetArr[1];
			
			if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_PRICE_VIEW))
			{
				String result = packetArr[2];
				switch (result)
				{
					case "1":
						String priceArr[] = packetArr[3].split(",");
						for (String priceInfo : priceArr)
						{
							String priceList[] = priceInfo.split("`");
							String priceType = priceList[0];
							String price = priceList[1];
							switch (priceType)
							{
								case "1":
								{
									t_type_1.setText(price);
									break;
								}
								case "2":
								{
									t_type_2.setText(price);
									break;
								}
								case "3":
								{
									t_type_3.setText(price);
									break;
								}
							}
						}
						break;
					case "2":
						System.out.println("가격정보 요청에 실패하였습니다.");
						mainGUI.alert("경고", "가격정보 요청에 실패하였습니다.");
						break;
				}
			}
			// ChargeDAO cDao = new ChargeDAO();
			// Iterator<ChargeDTO> cIter = cDao.getChargeList().iterator();
			// while (cIter.hasNext())
			// {
			// ChargeDTO temp = cIter.next();
			// switch (temp.getType())
			// {
			// case "1":
			// {
			// t_type_1.setText(Integer.toString(temp.getPrice()));
			// break;
			// }
			// case "2":
			// {
			// t_type_2.setText(Integer.toString(temp.getPrice()));
			// break;
			// }
			// case "3":
			// {
			// t_type_3.setText(Integer.toString(temp.getPrice()));
			// break;
			// }
			// }
			// }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
}
