package com.view;

import java.net.URL;
import java.util.ResourceBundle;

import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class PriceChange implements Initializable
{
	
	@FXML
	private TextField tf_morning;
	
	@FXML
	private TextField tf_afternoon;
	
	@FXML
	private TextField tf_night;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		try
		{
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
					{
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
									tf_morning.setText(price);
									break;
								}
								case "2":
								{
									tf_afternoon.setText(price);
									break;
								}
								case "3":
								{
									tf_night.setText(price);
									break;
								}
							}
						}
						break;
					}
					case "2":
					{
						mainGUI.alert("경고", "가격정보 요청에 실패하였습니다.");
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	@FXML
	void changePrice(ActionEvent event)
	{
		try
		{
			if (tf_morning.getText().equals("") || tf_afternoon.getText().equals("") || tf_night.getText().equals(""))
			{
				mainGUI.alert("경고", "모든 데이터를 입력해주세요");
				return;
			}
			String morning = tf_morning.getText();
			String afternoon = tf_afternoon.getText();
			String night = tf_night.getText();
			
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_PRICE_CHANGE + "`" + morning + "`" + afternoon + "`" + night);
			
			String packet = mainGUI.readLine();
			String packetArr[] = packet.split("`");
			String packetType = packetArr[0];
			String packetCode = packetArr[1];
			if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_PRICE_CHANGE))
			{
				String result = packetArr[2];
				switch (result)
				{
					case "1":
						mainGUI.alert("수정 완료", "데이터 수정 완료");
						break;
					case "2":
						mainGUI.alert("경고", "가격정보 수정 실패!");
						break;
				}
			}
		}
		catch (Exception e)
		{
			mainGUI.alert("오류", "DB 접속 오류");
			e.printStackTrace();
		}
	}
	
}
