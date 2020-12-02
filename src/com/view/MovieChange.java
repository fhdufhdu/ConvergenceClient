package com.view;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import com.db.model.MovieDTO;
import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import oracle.net.aso.p;

public class MovieChange
{
	private String is_current; // 현재 상영작 구분 용도
	private MovieDTO currentMov; // 현재 영화 객체 저장
	BorderPane parent; // 이전 parent를 기억하기 위한 용도
	
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
	
	@FXML
	private Button btn_movie_change;
	
	// 외부에서 받아오는 데이터들
	public void initData(MovieDTO mov, BorderPane parent)
	{
		this.parent = parent;
		currentMov = mov;
		is_current = currentMov.getIsCurrent();
		
		if (is_current.equals("0"))
		{
			cb_close.setSelected(true);
			cb_current.setSelected(false);
			cb_soon.setSelected(false);
		}
		else if (is_current.equals("1"))
		{
			cb_close.setSelected(false);
			cb_current.setSelected(true);
			cb_soon.setSelected(false);
		}
		else
		{
			cb_close.setSelected(false);
			cb_current.setSelected(false);
			cb_soon.setSelected(true);
		}
		
		tf_title.setPromptText(mov.getTitle());
		dp_release_date.setPromptText(mov.getReleaseDate().toString());
		tf_director.setPromptText(mov.getDirector());
		ta_actor.setPromptText(mov.getActor());
		tf_min.setPromptText(Integer.toString(mov.getMin()));
		tf_trailer.setPromptText(mov.getTrailerPath());
		ta_plot.setPromptText(mov.getPlot().replace("\\}", "\n"));
		ta_stillcut.setPromptText(mov.getStillCutPath());
		tf_poster.setPromptText(mov.getPosterPath());
	}
	
	@FXML
	void changeMovie(ActionEvent event)
	{
		try
		{
			String stillCut = "";
			if (ta_stillcut.getText().equals(""))
				stillCut = currentMov.getStillCutPath();
			else
				for (String temp : ta_stillcut.getText().split("\n"))
					stillCut += temp + " ";
			String plot = "";
			if (ta_plot.getText().equals(""))
				plot = currentMov.getPlot();
			else
				for (String temp : ta_plot.getText().split("\n"))
					plot += temp + "}";
				
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String id = currentMov.getId();
			String title = tf_title.getText().equals("") ? currentMov.getTitle() : tf_title.getText();
			String release_date = dp_release_date.getValue() == null ? currentMov.getReleaseDate().toString() : dateFormat.format(dp_release_date.getValue());
			String poster = tf_poster.getText().equals("") ? currentMov.getPosterPath() : tf_poster.getText();
			String trailer = tf_trailer.getText().equals("") ? currentMov.getTrailerPath() : tf_trailer.getText();
			String director = tf_director.getText().equals("") ? currentMov.getDirector() : tf_director.getText();
			String actor = ta_actor.getText().equals("") ? currentMov.getActor() : tf_trailer.getText();
			String min = tf_min.getText().equals("") ? Integer.toString(currentMov.getMin()) : tf_min.getText();
			
			System.out.println(ta_stillcut.getText().split("\n").length);
			
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_MOVIE_CHANGE + "`" + id + "`" + title + "`" + release_date + "`" + is_current + "`" + plot + "`" + poster + "`" + stillCut + "`" + trailer + "`" + director + "`" + actor + "`" + min);
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`");
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_MOVIE_CHANGE))
				{
					String pakcet_result = packetArr[2];
					switch (pakcet_result)
					{
						case "1":
						{
							mainGUI.alert("수정완료", "수정완료 되었습니다!");
							
							FXMLLoader loader = new FXMLLoader(TheaterManage.class.getResource("./xml/admin_sub_page/movie_manage.fxml"));
							Parent root = (Parent) loader.load();
							parent.setCenter(root);
							return;
						}
						case "2":
						{
							mainGUI.alert("수정실패", "수정실패입니다!");
							return;
						}
					}
				}
			}
		}
		catch (NumberFormatException e)
		{
			mainGUI.alert("상영시간", "상영시간에는 숫자를 입력해주세요!");
		}
		catch (SQLException e)
		{
			mainGUI.alert("DB서버 연결오류", "잠시 후 다시 시도해주세요!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@FXML
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
}
