package com.view;

import java.time.format.DateTimeFormatter;

import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class MovieAdd
{
	private String is_current;
	
	@FXML
	private CheckBox cb_current;
	
	@FXML
	private CheckBox cb_close;
	
	@FXML
	private CheckBox cb_soon;
	
	@FXML
	private TextField tf_title;
	
	@FXML
	private DatePicker dp_release_date;
	
	@FXML
	private TextField tf_director;
	
	@FXML
	private TextArea ta_actor;
	
	@FXML
	private TextField tf_min;
	
	@FXML
	private TextField tf_poster;
	
	@FXML
	private TextArea ta_stillcut;
	
	@FXML
	private TextField tf_trailer;
	
	@FXML
	private TextArea ta_plot;
	
	@FXML
	private Text result;
	
	@FXML // 영화 추가 버튼 눌렀을 때
	void addMovie(ActionEvent event) throws Exception
	{
		try
		{
			// 입력 필드 모두 채웠는지 확인
			if (is_current == null || tf_title.getText().equals("") || dp_release_date.getValue() == null || tf_director.getText().equals("") || ta_actor.getText().equals("") || tf_min.getText().equals("") || tf_poster.getText().equals("") || ta_stillcut.getText().equals("") || tf_trailer.getText().equals("") || ta_plot.getText().equals(""))
			{
				alert("입력오류", "모든 필드를 채워주세요!");
			}
			
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			
			String stillCut = "";
			for (String temp : ta_stillcut.getText().split("\n"))
				stillCut += temp + " ";
			String plot = "";
			for (String temp : ta_plot.getText().split("\n"))
				plot += temp + "}";
			
			String title = tf_title.getText();
			String release_date = dateFormat.format(dp_release_date.getValue());
			String poster = tf_poster.getText();
			String trailer = tf_trailer.getText();
			String director = tf_director.getText();
			String actor = ta_actor.getText();
			String min = tf_min.getText();
			
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_MOVIE_ADD + "`" + title + "`" + release_date + "`" + is_current + "`" + plot + "`" + poster + "`" + stillCut + "`" + trailer + "`" + director + "`" + actor + "`" + min);
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`");
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_MOVIE_ADD))
				{
					String pakcet_result = packetArr[2];
					switch (pakcet_result)
					{
						case "1":
						{
							mainGUI.alert("등록완료", "등록완료 되었습니다!");
							return;
						}
						case "2":
						{
							mainGUI.alert("등록 실패", "이미 존재하는 영화가 있습니다!");
							return;
						}
						case "3":
						{
							mainGUI.alert("등록실패", "등록실패 되었습니다!");
							return;
						}
					}
				}
			}
		}
		catch (NumberFormatException e)
		{
			alert("상영시간", "상영시간에는 숫자를 입력해주세요!");
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@FXML // 각각 버튼 눌렀을 때 하나만 선택 가능하게 하기
	void selectClose(ActionEvent event)
	{
		cb_current.setSelected(false);
		cb_soon.setSelected(false);
		is_current = "0";
	}
	
	@FXML
	void selectCurrent(ActionEvent event)
	{
		cb_soon.setSelected(false);
		cb_close.setSelected(false);
		is_current = "1";
	}
	
	@FXML
	void selectSoon(ActionEvent event)
	{
		cb_current.setSelected(false);
		cb_close.setSelected(false);
		is_current = "2";
	}
	
	private void alert(String head, String msg)
	{
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("경고");
		alert.setHeaderText(head);
		alert.setContentText(msg);
		
		alert.showAndWait(); // Alert창 보여주기
	}
	
}
