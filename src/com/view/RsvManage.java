package com.view;

import java.net.URL;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class RsvManage implements Initializable
{
	// 리스트뷰, 테이블뷰를 위한 리스트
	private ObservableList<MemberDTO> member_id_list;
	private ObservableList<MovieDTO> movie_title_list;
	private ObservableList<TheaterDTO> theater_list;
	private ObservableList<CustomDTO> custom_list;
	
	// 리스트, 테이블 뷰에서 선택된 객체
	private MemberDTO selectedMem;
	private MovieDTO selectedMov;
	private TheaterDTO selectedThea;
	private CustomDTO selectedCustom;
	
	@FXML
	private TableView<CustomDTO> tv_reservation;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_user;
	
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
	private TextField tf_member_id;
	
	@FXML
	private ListView<MemberDTO> lv_member;
	
	@FXML
	private TextField tf_movie_name;
	
	@FXML
	private TextField tf_theater_name;
	
	@FXML
	private ListView<MovieDTO> lv_movie;
	
	@FXML
	private ListView<TheaterDTO> lv_theater;
	
	@FXML
	private DatePicker dp_start_date;
	
	@FXML
	private Button btn_search;
	
	@FXML
	private DatePicker dp_end_date;
	
	// 초기화
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		try
		{
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_MEMBER_VIEW);
			member_id_list = FXCollections.observableArrayList();
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_MEMBER_VIEW))
				{
					String result = packetArr[2];
					
					switch (result)
					{
						case "1":
						{
							String memberList = packetArr[3];
							String listArr[] = memberList.split("\\{"); // 각 회원 별로 리스트 분할
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("\\|"); // 회원 별 정보 분할
								String id = infoArr[0];
								String name = infoArr[1];
								String password = infoArr[2];
								String role = infoArr[3];
								String gender = infoArr[4];
								String phone_number = infoArr[5];
								String birth = infoArr[6];
								String account = infoArr[7];
								
								member_id_list.add(new MemberDTO(id, role, password, account, name, phone_number, birth, gender));
							}
							// 리스트뷰 세팅
							lv_member.setItems(FXCollections.observableArrayList());
							lv_member.getItems().addAll(member_id_list);
							lv_member.setOnMouseClicked((MouseEvent) ->
							{
								if (lv_member.getSelectionModel().getSelectedItem() == null)
									return;
								selectedMem = lv_member.getSelectionModel().getSelectedItem();
								lv_member.setMaxHeight(0);
								lv_member.getItems().clear();
								tf_member_id.setText(selectedMem.getId());
							});
							// 리스트뷰에 담을 객체
							lv_member.setCellFactory(lv -> new ListCell<MemberDTO>()
							{
								@Override
								protected void updateItem(MemberDTO item, boolean empty)
								{
									super.updateItem(item, empty);
									setText(item == null ? null : item.getId());
								}
							});
							break;
						}
						case "2":
						{
							mainGUI.alert("오류", "회원 리스트가 없습니다.");
							break;
						}
						case "3":
						{
							mainGUI.alert("오류", "회원 리스트 요청 실패했습니다.");
							break;
						}
					}
					if (result != null)
						break;
				}
			}
			
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_MOVIE_VIEW + "`%`1976-01-01`2222-01-01`%`%`%`0");
			movie_title_list = FXCollections.observableArrayList();
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_MOVIE_VIEW))
				{
					String result = packetArr[2];
					
					switch (result)
					{
						case "1":
						{
							String movieList = packetArr[3];
							String listArr[] = movieList.split("\\{"); // 각 영화별로 리스트 분할
							
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("\\|"); // 영화 별 정보 분할
								String mv_id = infoArr[0];
								String mv_title = infoArr[1];
								String mv_release_date = infoArr[2];
								String mv_is_current = infoArr[3];
								String mv_plot = infoArr[4];
								String mv_poster_path = infoArr[5];
								String mv_stillCut_path = infoArr[6];
								String mv_trailer_path = infoArr[7];
								String mv_director = infoArr[8];
								String mv_actor = infoArr[9];
								int mv_min = Integer.parseInt(infoArr[10]);
								
								movie_title_list.add(new MovieDTO(mv_id, mv_title, mv_release_date, mv_is_current, mv_plot, mv_poster_path, mv_stillCut_path, mv_trailer_path, mv_director, mv_actor, mv_min));
								
								lv_movie.setItems(FXCollections.observableArrayList());
								lv_movie.getItems().addAll(movie_title_list);
								lv_movie.setOnMouseClicked((MouseEvent) ->
								{
									if (lv_movie.getSelectionModel().getSelectedItem() == null)
										return;
									selectedMov = lv_movie.getSelectionModel().getSelectedItem();
									lv_movie.setMaxHeight(0);
									lv_movie.getItems().clear();
									tf_movie_name.setText(selectedMov.getTitle());
								});
								lv_movie.setCellFactory(lv -> new ListCell<MovieDTO>()
								{
									@Override
									protected void updateItem(MovieDTO item, boolean empty)
									{
										super.updateItem(item, empty);
										setText(item == null ? null : item.getTitle());
									}
								});
							}
							break;
						}
						case "2":
						{
							mainGUI.alert("영화 리스트", "영화 리스트가 없습니다.");
							break;
						}
						case "3":
						{
							mainGUI.alert("영화 리스트", "영화 리스트 요청 실패했습니다.");
							break;
						}
					}
					if (result != null)
						break;
				}
			}
			
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_THEATER_VIEW);
			theater_list = FXCollections.observableArrayList();
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_THEATER_VIEW))
				{
					String result = packetArr[2];
					
					switch (result)
					{
						case "1":
						{
							String theaterList = packetArr[3];
							String listArr[] = theaterList.split("\\{"); // 각 영화관 별로 리스트 분할
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("\\|"); // 영화관 별 정보 분할
								String id = infoArr[0];
								String name = infoArr[1];
								String address = infoArr[2];
								String screen = infoArr[3];
								String seat = infoArr[4];
								
								theater_list.add(new TheaterDTO(id, name, address, Integer.parseInt(screen), Integer.parseInt(seat)));
							}
							
							lv_theater.setItems(FXCollections.observableArrayList());
							lv_theater.getItems().addAll(theater_list);
							lv_theater.setOnMouseClicked((MouseEvent) ->
							{
								if (lv_theater.getSelectionModel().getSelectedItem() == null)
									return;
								selectedThea = lv_theater.getSelectionModel().getSelectedItem();
								lv_theater.setMaxHeight(0);
								lv_theater.getItems().clear();
								tf_theater_name.setText(selectedThea.getName());
							});
							lv_theater.setCellFactory(lv -> new ListCell<TheaterDTO>()
							{
								@Override
								protected void updateItem(TheaterDTO item, boolean empty)
								{
									super.updateItem(item, empty);
									setText(item == null ? null : item.getName());
								}
							});
							break;
						}
						case "2":
						{
							mainGUI.alert("오류", "영화관 리스트가 없습니다.");
							break;
						}
						case "3":
						{
							mainGUI.alert("오류", "영화관 리스트 요청 실패했습니다.");
							break;
						}
					}
					if (result != null)
						break;
				}
			}
			
			custom_list = FXCollections.observableArrayList();
			
			// 리스트 초기화
			initList();
			
			// 각 테이블뷰 컬럼에 어떠한 값이 들어갈 것인지 세팅
			tc_user.setCellValueFactory(cellData -> cellData.getValue().getMember());
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
	
	@FXML // 사용자 리스트뷰 요소 선택시
	void clickedLvMember(MouseEvent event)
	{
		lv_member.setMaxHeight(0);
	}
	
	@FXML // 사용자 필드에 값 입력시
	void typedTfMember(KeyEvent event)
	{
		// 필드에 값이 없다면 리스트뷰 감추기
		if (tf_member_id.getText().equals(""))
		{
			selectedMem = null;
			lv_member.getItems().clear();
			lv_member.setMaxHeight(0);
			return;
		}
		
		// 값이 있으면 리스트뷰 활성화
		lv_member.setMaxHeight(130);
		lv_member.setItems(FXCollections.observableArrayList());
		ObservableList<MemberDTO> temp_list = FXCollections.observableArrayList();
		for (int i = 0; i < member_id_list.size(); i++)
		{
			// 모든 사용자 리스트에서 현재 입력한 값을 포함하는 요소가 있는지 확인
			if (member_id_list.get(i).getId().contains(tf_member_id.getText()))
			{
				temp_list.add(member_id_list.get(i));
			}
		}
		lv_member.getItems().addAll(temp_list);
	}
	
	@FXML // 영화 리스트뷰 요소 선택시
	void clickedLvMovie(MouseEvent event)
	{
		lv_movie.setMaxHeight(0);
	}
	
	@FXML // 영화 필드에 값 입력시
	void typedTfMovie(KeyEvent event)
	{
		// 필드에 값이 없다면 리스트뷰 감추기
		if (tf_movie_name.getText().equals(""))
		{
			selectedMov = null;
			lv_movie.getItems().clear();
			lv_movie.setMaxHeight(0);
			return;
		}
		
		// 값이 있으면 리스트뷰 활성화
		lv_movie.setMaxHeight(130);
		lv_movie.setItems(FXCollections.observableArrayList());
		ObservableList<MovieDTO> temp_list = FXCollections.observableArrayList();
		for (int i = 0; i < movie_title_list.size(); i++)
		{
			// 모든 사용자 리스트에서 현재 입력한 값을 포함하는 요소가 있는지 확인
			if (movie_title_list.get(i).getTitle().contains(tf_movie_name.getText()))
			{
				temp_list.add(movie_title_list.get(i));
			}
		}
		lv_movie.getItems().addAll(temp_list);
	}
	
	@FXML // 영화관 리스트뷰 요소 선택시
	void clickedLvTheater(MouseEvent event)
	{
		lv_theater.setMaxHeight(0);
	}
	
	@FXML // 영화관 필드에 값 입력시
	void typedTfTheater(KeyEvent event)
	{
		// 필드에 값이 없다면 리스트뷰 감추기
		if (tf_theater_name.getText().equals(""))
		{
			selectedThea = null;
			lv_theater.getItems().clear();
			lv_theater.setMaxHeight(0);
			return;
		}
		
		// 값이 있으면 리스트뷰 활성화
		lv_theater.setMaxHeight(130);
		lv_theater.setItems(FXCollections.observableArrayList());
		ObservableList<TheaterDTO> temp_list = FXCollections.observableArrayList();
		for (int i = 0; i < theater_list.size(); i++)
		{
			// 모든 사용자 리스트에서 현재 입력한 값을 포함하는 요소가 있는지 확인
			if (theater_list.get(i).getName().contains(tf_theater_name.getText()))
			{
				temp_list.add(theater_list.get(i));
			}
		}
		lv_theater.getItems().addAll(temp_list);
	}
	
	// 예매 내역 검색
	@FXML
	void getRsvSearch(ActionEvent event)
	{
		initList();
	}
	
	// 예매내역 삭제
	@FXML
	void deleteRsv(ActionEvent event) throws Exception
	{
		try
		{
			if (selectedCustom == null)
			{
				mainGUI.alert("경고", "데이터를 선택해주세요");
				return;
			}
			
			ButtonType btnType = mainGUI.confirm("취소확인", "정말로 취소하시겠습니까?");
			if (btnType != ButtonType.OK)
			{
				return;
			}
			
			String selectedType = selectedCustom.getRsv().getType(); // 예매타입, 1이면 예매 2이면 취소
			if (selectedType.equals("2")) // 취소된 예매 중복취소 막기 위한 제어문
			{
				mainGUI.alert("경고", "이미 취소된 예매 내역입니다.");
				return;
			}
			
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_RESERVATION_DELETE + "`" + selectedCustom.getRsv().getId());
			
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
							break;
						}
						case "2":
						{
							mainGUI.alert("취소 에러", "예매 취소에 실패하였습니다.");
							break;
						}
					}
					if (result != null)
						break;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
		}
	}
	
	// 리스트 초기화
	private void initList()
	{
		try
		{
			custom_list.clear();
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			// 리스트뷰를 선택했는가 안했는가에 따라서 전체 검색 혹은 특정 값 검색
			String mem_id = selectedMem == null ? "%" : selectedMem.getId();
			String mov_id = selectedMov == null ? "%" : selectedMov.getId();
			String thea_id = selectedThea == null ? "%" : selectedThea.getId();
			String start_date = dp_start_date.getValue() == null ? "1976-01-01" : dateFormat.format(dp_start_date.getValue());
			String end_date = dp_end_date.getValue() == null ? "2222-01-01" : dateFormat.format(dp_end_date.getValue());
			
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_RESERVATION_VIEW + "`" + mem_id + "`" + mov_id + "`" + thea_id + "`" + start_date + "`" + end_date);
			
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
								String infoArr[] = listInfo.split("\\|");
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
								
								c_list.add(new CustomDTO(new ReservationDTO(id, member_id, time_table_id, screen_row, screen_col, price, type, rsv_time, account, bank)));
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
	
	// 테이블 뷰 출력을 위한 DTO의 모음
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
								String sc_info[] = listArr[0].split("\\|"); // 상영관 정보 분할
								String mv_info[] = listArr[1].split("\\|"); // 영화 정보 분할
								String th_info[] = listArr[2].split("\\|"); // 영화관 정보 분할
								String mem_info[] = listArr[3].split("\\|"); // 회원 정보 분할
								String tt_info[] = listArr[4].split("\\|"); // 상영시간표 정보 분할
								
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
