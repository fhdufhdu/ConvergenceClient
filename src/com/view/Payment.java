package com.view;

import java.util.ArrayList;
import java.util.Iterator;

import com.db.model.MovieDTO;
import com.db.model.ScreenDTO;
import com.db.model.TheaterDTO;
import com.db.model.TimeTableDTO;
import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Payment
{
	private int price;
	private TheaterDTO theater;
	private ScreenDTO screen;
	private MovieDTO movie;
	private TimeTableDTO timetable;
	private ArrayList<Integer> row_list;
	private ArrayList<Integer> col_list;
	
	@FXML
	private TextField tf_bank;
	
	@FXML
	private TextField tf_account;
	
	@FXML
	private Button btn_payment;
	
	@FXML
	private Text t_theater;
	
	@FXML
	private Text t_movie;
	
	@FXML
	private Text t_screen;
	
	@FXML
	private Text t_date;
	
	@FXML
	private Text t_start_time;
	
	@FXML
	private Text t_seat;
	
	@FXML
	private Text t_price;
	
	@FXML
	private PasswordField pf_passwd;
	
	@FXML
	void getPayment(ActionEvent event) throws Exception
	{
		try
		{
			String member_id = Login.USER_ID;
			String timetable_id = timetable.getId();
			String account = tf_account.getText();
			String bank = tf_bank.getText();
			String passwd = pf_passwd.getText();
			String rowList = "";
			String colList = "";
			
			Iterator<Integer> riter = row_list.iterator();
			Iterator<Integer> citer = col_list.iterator();
			
			while (riter.hasNext())
				rowList += Integer.toString(riter.next()) + "|";
			
			while (citer.hasNext())
				colList += Integer.toString(citer.next()) + "|";
			
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_PAYMENT_ADD + "`" + member_id + "`" + timetable_id + "`" + rowList + "`" + colList + "`" + account + "`" + bank + "`" + passwd);
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_PAYMENT_ADD))
				{
					String result = packetArr[2];
					
					switch (result)
					{
						case "1":
						{
							mainGUI.alert("결제 완료", "정상적으로 예매가 처리되었습니다");
							Stage stage = (Stage) btn_payment.getScene().getWindow();
							stage.close();
							return;
						}
						case "2":
						{
							mainGUI.alert("결제 오류", "결제 오류 발생, 결제 정보를 확인해주세요");
							return;
						}
						case "3":
						{
							mainGUI.alert("결제 오류", "좌석 정보 오류 발생");
							return;
						}
						case "4":
						{
							mainGUI.alert("오류", "오류 발생");
							return;
						}
					}
				}
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			mainGUI.alert("오류", "오류 발생");
		}
	}
	
	public void initData(TheaterDTO theater, ScreenDTO screen, MovieDTO movie, TimeTableDTO timetable, ArrayList<ArrayList<Integer>> seat_list, int price)
	{
		String date[] = timetable.getStartTime().toString().split(" ");
		row_list = seat_list.get(0);
		col_list = seat_list.get(1);
		
		this.price = price;
		this.theater = theater;
		this.screen = screen;
		this.movie = movie;
		this.timetable = timetable;
		
		t_theater.setText(t_theater.getText() + theater.getName());
		t_movie.setText(t_movie.getText() + movie.getTitle());
		t_screen.setText(t_screen.getText() + screen.getName() + "관");
		t_date.setText(t_date.getText() + date[0]);
		t_start_time.setText(t_start_time.getText() + date[1]);
		t_price.setText(t_price.getText() + price + "원");
		for (int i = 0; i < row_list.size(); i++)
		{
			t_seat.setText(t_seat.getText() + row_list.get(i) + col_list.get(i) + "`");
		}
	}
	
}
