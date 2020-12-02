package com.view;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.db.model.MemberDTO;
import com.db.model.MovieDTO;
import com.db.model.ReservationDTO;
import com.db.model.ScreenDTO;
import com.db.model.TheaterDTO;
import com.db.model.TimeTableDTO;
import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class RsvCancel implements Initializable
{
	private ObservableList<CustomDTO> custom_list;
	private CustomDTO selectedCustom;
	
	@FXML
	private TableView<CustomDTO> tv_reservation;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_movie;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_theater;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_screen;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_date;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_start_time;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_end_time;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_seat;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_price;
	
	@FXML
	private Button btn_cancel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		try
		{
			custom_list = FXCollections.observableArrayList();
			
			// 리스트 초기화
			initList();
			
			// 각 테이블뷰 컬럼에 어떠한 값이 들어갈 것인지 세팅
			tc_movie.setCellValueFactory(cellData -> cellData.getValue().getMovie());
			tc_theater.setCellValueFactory(cellData -> cellData.getValue().getTheater());
			tc_screen.setCellValueFactory(cellData -> cellData.getValue().getScreen());
			tc_start_time.setCellValueFactory(cellData -> cellData.getValue().getStartTime());
			tc_end_time.setCellValueFactory(cellData -> cellData.getValue().getEndTime());
			tc_seat.setCellValueFactory(cellData -> cellData.getValue().getSeat());
			tc_price.setCellValueFactory(cellData -> cellData.getValue().getPrice());
			
			// 테이블 뷰와 리스트를 연결
			tv_reservation.setItems(custom_list);
			
			// 테이블 뷰 row 선택 시 발생하는 이벤트 지정
			tv_reservation.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CustomDTO>()
			{
				@Override
				public void changed(ObservableValue<? extends CustomDTO> observable, CustomDTO oldValue, CustomDTO newValue)
				{
					selectedCustom = tv_reservation.getSelectionModel().getSelectedItem();
				}
			});
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@FXML
	void getCancel(ActionEvent event) throws Exception
	{
		try
		{
			if (tv_reservation.getSelectionModel().isEmpty())
			{
				mainGUI.alert("취소 오류", "취소할 데이터를 선택해주세요");
				return;
			}
			
			ButtonType btnType = mainGUI.confirm("취소 확인", "정말로 취소하시겠습니까?");
			if (btnType != ButtonType.OK)
			{
				return;
			}
			
			String rsv_id = selectedCustom.getRsv().getId();
			
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_RESERVATION_DELETE + "`" + rsv_id);
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_RESERVATION_DELETE))
				{
					String result = packetArr[2];
					
					switch (result)
					{
						case "1":
						{
							mainGUI.alert("취소 완료", "예매 취소에 성공하였습니다.");
							initList();
							return;
						}
						case "2":
						{
							mainGUI.alert("취소 에러", "예매 취소가 불가합니다.");
							return;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void initList() throws Exception
	{
		try
		{
			custom_list.clear();
			String login_id = Login.USER_ID;
			
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_RESERVATION_VIEW + "`" + login_id + "`null");
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_RESERVATION_VIEW))
				{
					String result = packetArr[2];
					
					switch (result)
					{
						case "1":
						{
							String reservationList = packetArr[3];
							String listArr[] = reservationList.split("\\{");
							ArrayList<CustomDTO> c_list = new ArrayList<CustomDTO>();
							
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("|");
								String id = infoArr[0];
								String member_id = infoArr[1];
								String time_table_id = infoArr[2];
								int screen_row = Integer.parseInt(infoArr[3]);
								int screen_col = Integer.parseInt(infoArr[4]);
								int price = Integer.parseInt(infoArr[5]);
								String type = infoArr[6];
								String rsv_time = infoArr[7];
								String account = infoArr[8];
								String bank = infoArr[9];
								
								ReservationDTO rsvDto = new ReservationDTO(id, member_id, time_table_id, screen_row, screen_col, price, type, rsv_time, account, bank);
								
								if (rsvDto.getType().equals("2"))
									continue;
								
								c_list.add(new CustomDTO(rsvDto));
							}
							if (c_list.size() == 0)
							{
								mainGUI.alert("경고", "해당 예매 리스트가 없습니다.");
							}
							custom_list.addAll(c_list);
							return;
						}
						case "2":
						{
							mainGUI.alert("오류", "예매 리스트를 불러오는데 실패했습니다.");
							return;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private class CustomDTO
	{
		ReservationDTO rDto;
		MemberDTO memDto;
		TimeTableDTO ttDto;
		MovieDTO movDto;
		ScreenDTO sDto;
		TheaterDTO tDto;
		
		public CustomDTO(ReservationDTO rDto)
		{
			try
			{
				mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_CUSTOM_INFO + "`2`" + rDto.getTimeTableId() + "`" + rDto.getMemberId());
				
				while (true)
				{
					String packet = mainGUI.readLine();
					String packetArr[] = packet.split("`"); // 패킷 분할
					String packetType = packetArr[0];
					String packetCode = packetArr[1];
					
					if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_CUSTOM_INFO))
					{
						String result = packetArr[2];
						
						switch (result)
						{
							case "1":
							{
								this.rDto = rDto;
								String infoList = packetArr[3];
								String listArr[] = infoList.split("\\{"); // 각 리스트 분할
								String sc_info[] = listArr[0].split("|"); // 상영관 정보 분할
								String mv_info[] = listArr[1].split("|"); // 영화 정보 분할
								String th_info[] = listArr[2].split("|"); // 영화관 정보 분할
								String mem_info[] = listArr[3].split("|"); // 회원 정보 분할
								String tt_info[] = listArr[4].split("|"); // 상영시간표 정보 분할
								
								sDto = new ScreenDTO(sc_info[0], sc_info[1], sc_info[2], Integer.valueOf(sc_info[3]), Integer.valueOf(sc_info[4]), Integer.valueOf(sc_info[5]));
								movDto = new MovieDTO(mv_info[0], mv_info[1], mv_info[2], mv_info[3], mv_info[4], mv_info[5], mv_info[6], mv_info[7], mv_info[8], mv_info[9], Integer.valueOf(mv_info[10]));
								tDto = new TheaterDTO(th_info[0], th_info[1], th_info[2], Integer.valueOf(th_info[3]), Integer.valueOf(th_info[4]));
								memDto = new MemberDTO(mem_info[0], mem_info[1], mem_info[2], mem_info[3], mem_info[4], mem_info[5], mem_info[6], mem_info[7]);
								ttDto = new TimeTableDTO(tt_info[0], tt_info[1], tt_info[2], tt_info[3], tt_info[4], tt_info[5], Integer.valueOf(tt_info[6]));
								return;
							}
							case "2":
							{
								mainGUI.alert("경고", "정보 요청 실패했습니다.");
								return;
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		public StringProperty getMember()
		{
			return new SimpleStringProperty(memDto.getName());
		}
		
		public StringProperty getMovie()
		{
			return new SimpleStringProperty(movDto.getTitle());
		}
		
		public StringProperty getTheater()
		{
			return new SimpleStringProperty(tDto.getName());
		}
		
		public StringProperty getScreen()
		{
			return new SimpleStringProperty(sDto.getName());
		}
		
		public StringProperty getStartTime()
		{
			return new SimpleStringProperty(ttDto.getStartTime().toString());
		}
		
		public StringProperty getEndTime()
		{
			return new SimpleStringProperty(ttDto.getEndTime().toString());
		}
		
		public StringProperty getSeat()
		{
			return new SimpleStringProperty(Character.toString((char) (rDto.getScreenRow() + 65)) + Integer.toString(rDto.getScreenCol() + 1));
		}
		
		public StringProperty getPrice()
		{
			return new SimpleStringProperty(Integer.toString(rDto.getPrice()));
		}
		
		public ReservationDTO getRsv()
		{
			return rDto;
		}
	}
	
}
