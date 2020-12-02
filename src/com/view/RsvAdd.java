package com.view;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import com.db.model.MemberDTO;
import com.db.model.MovieDTO;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RsvAdd implements Initializable
{
	private ObservableList<TheaterDTO> theater_list;
	private ObservableList<ScreenDTO> screen_list;
	private ObservableList<MovieDTO> movie_list;
	private ObservableList<MemberDTO> member_list;
	private ObservableList<CustomDTO> custom_list;
	
	private ArrayList<Integer> row_list;
	private ArrayList<Integer> col_list;
	
	private TheaterDTO selectedThea;
	private ScreenDTO selectedScreen;
	private MovieDTO selectedMovie;
	private MemberDTO selectedMember;
	private CustomDTO selectedCustom;
	@FXML
	private ListView<TheaterDTO> lv_theater;
	
	@FXML
	private ListView<ScreenDTO> lv_screen;
	
	@FXML
	private ListView<MovieDTO> lv_movie;
	
	@FXML
	private ListView<MemberDTO> lv_member;
	
	@FXML
	private TableView<CustomDTO> tv_timetable;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_theater;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_screen;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_movie;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_start_time;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_end_time;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_current;
	
	@FXML
	private Text result;
	
	@FXML
	private Button btn_select_seat;
	
	@FXML
	private TextField tf_theater;
	
	@FXML
	private TextField tf_screen;
	
	@FXML
	private TextField tf_movie;
	
	@FXML
	private DatePicker dp_start_date;
	
	@FXML
	private MenuButton mb_meridiem_start;
	
	@FXML
	private MenuButton mb_hours_start;
	
	@FXML
	private MenuButton mb_minutes_start;
	
	@FXML
	private MenuButton mb_meridiem_end;
	
	@FXML
	private MenuButton mb_hours_end;
	
	@FXML
	private MenuButton mb_minutes_end;
	
	@FXML
	private Button btn_search_timetable;
	
	@FXML
	private TextField tf_member;
	
	@FXML
	private TextField tf_bank;
	
	@FXML
	private TextField tf_account;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		try
		{
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_MEMBER_VIEW);
			member_list = FXCollections.observableArrayList();
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("!"); // 패킷 분할
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
							String listArr[] = memberList.split(","); // 각 회원 별로 리스트 분할
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("`"); // 회원 별 정보 분할
								String id = infoArr[0];
								String name = infoArr[1];
								String password = infoArr[2];
								String role = infoArr[3];
								String gender = infoArr[4];
								String phone_number = infoArr[5];
								String birth = infoArr[6];
								String account = infoArr[7];
								
								member_list.add(new MemberDTO(id, role, password, account, name, phone_number, birth, gender));
							}
							lv_member.setItems(FXCollections.observableArrayList());
							lv_member.getItems().addAll(member_list);
							lv_member.setOnMouseClicked((MouseEvent) ->
							{
								if (lv_member.getSelectionModel().getSelectedItem() == null)
									return;
								selectedMember = lv_member.getSelectionModel().getSelectedItem();
								lv_member.setMaxHeight(0);
								lv_member.getItems().clear();
								tf_member.setText(selectedMember.getId());
							});
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
			
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_THEATER_VIEW);
			theater_list = FXCollections.observableArrayList();
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("!"); // 패킷 분할
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
							String listArr[] = theaterList.split(","); // 각 영화관 별로 리스트 분할
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("`"); // 영화관 별 정보 분할
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
								tf_theater.setText(selectedThea.getName());
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
			
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_MOVIE_VIEW + "`%`1976-01-01`2222-01-01`%`%`%`0");
			movie_list = FXCollections.observableArrayList();
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("!"); // 패킷 분할
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
							String listArr[] = movieList.split(","); // 각 영화별로 리스트 분할
							
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("`"); // 영화 별 정보 분할
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
								
								movie_list.add(new MovieDTO(mv_id, mv_title, mv_release_date, mv_is_current, mv_plot, mv_poster_path, mv_stillCut_path, mv_trailer_path, mv_director, mv_actor, mv_min));
							}
							lv_movie.setItems(FXCollections.observableArrayList());
							lv_movie.getItems().addAll(movie_list);
							lv_movie.setOnMouseClicked((MouseEvent) ->
							{
								if (lv_movie.getSelectionModel().getSelectedItem() == null)
									return;
								selectedMovie = lv_movie.getSelectionModel().getSelectedItem();
								lv_movie.setMaxHeight(0);
								lv_movie.getItems().clear();
								tf_movie.setText(selectedMovie.getTitle());
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
			
			custom_list = FXCollections.observableArrayList();
			
			// 테이블 뷰 초기화
			tv_timetable.getItems().clear();
			
			// 각 테이블뷰 컬럼에 어떠한 값이 들어갈 것인지 세팅
			tc_theater.setCellValueFactory(cellData -> cellData.getValue().getTheater());
			tc_screen.setCellValueFactory(cellData -> cellData.getValue().getScreen());
			tc_movie.setCellValueFactory(cellData -> cellData.getValue().getMovie());
			tc_start_time.setCellValueFactory(cellData -> cellData.getValue().getStartTime());
			tc_end_time.setCellValueFactory(cellData -> cellData.getValue().getEndTime());
			tc_current.setCellValueFactory(cellData -> cellData.getValue().getCurrent());
			
			// 테이블 뷰와 리스트를 연결
			tv_timetable.setItems(custom_list);
			
			// 테이블 뷰 row 선택 시 발생하는 이벤트 지정
			tv_timetable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CustomDTO>()
			{
				@Override
				public void changed(ObservableValue<? extends CustomDTO> observable, CustomDTO oldValue, CustomDTO newValue)
				{
					if (tv_timetable.getSelectionModel().getSelectedItem() == null)
						return;
					selectedCustom = tv_timetable.getSelectionModel().getSelectedItem();
					System.out.println(selectedCustom.getTimeTable().getId());
				}
			});
			
			for (int i = 0; i < 24; i++)
			{
				MenuItem hour_s = new MenuItem(Integer.toString(i + 1) + "시");
				hour_s.setOnAction(new EventHandler<ActionEvent>()
				{
					public void handle(ActionEvent event)
					{
						mb_hours_start.setText(hour_s.getText());
					}
				});
				mb_hours_start.getItems().add(hour_s);
				
				MenuItem hour_e = new MenuItem(Integer.toString(i + 1) + "시");
				hour_e.setOnAction(new EventHandler<ActionEvent>()
				{
					public void handle(ActionEvent event)
					{
						mb_hours_end.setText(hour_e.getText());
					}
				});
				mb_hours_end.getItems().add(hour_e);
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	@FXML
	void addRsv(ActionEvent event) throws Exception
	{
		try
		{
			String member = tf_member.getText();
			String timetable_id = selectedCustom.getTimeTable().getId();
			String account = tf_account.getText();
			String bank = tf_bank.getText();
			String rowList = "";
			String colList = "";
			
			Iterator<Integer> riter = row_list.iterator();
			Iterator<Integer> citer = col_list.iterator();
			
			while (riter.hasNext())
				rowList += Integer.toString(riter.next()) + ",";
			
			while (citer.hasNext())
				colList += Integer.toString(citer.next()) + ",";
			
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_ADMINRESERVATION_ADD + "`" + member + "`" + timetable_id + "`" + rowList + "`" + colList + "`" + account + "`" + bank);
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_ADMINRESERVATION_ADD))
				{
					String result = packetArr[2];
					
					switch (result)
					{
						case "1":
						{
							mainGUI.alert("예매 성공", "예매에 성공했습니다");
							break;
						}
						case "2":
						{
							mainGUI.alert("에러", "중복되는 예매 발견");
							break;
						}
						case "3":
						{
							mainGUI.alert("에러", "가예매의 존재가 없음");
							break;
						}
						case "4":
						{
							mainGUI.alert("예매 실패", "예매에 실패했습니다");
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
			mainGUI.alert("예매 실패", "예매에 실패했습니다");
			e.printStackTrace();
		}
	}
	
	@FXML
	void clickedLvMovie(MouseEvent event)
	{
		
	}
	
	@FXML
	void clickedLvScreen(MouseEvent event)
	{
		
	}
	
	@FXML
	void clickedLvTheater(MouseEvent event)
	{
		
	}
	
	@FXML
	void clickedLvMember(MouseEvent event)
	{
		
	}
	
	@FXML
	void clickedTfMovie(MouseEvent event)
	{
		lv_movie.getItems().clear();
		lv_movie.getItems().addAll(movie_list);
		lv_movie.setMaxHeight(130);
	}
	
	@FXML
	void clickedTfScreen(MouseEvent event)
	{
		try
		{
			if (selectedThea == null)
			{
				mainGUI.alert("오류", "영화관을 선택하세요.");
				return;
			}
			
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_SCREEN_VIEW + "`" + selectedThea.getId());
			screen_list = FXCollections.observableArrayList();
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("!"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_SCREEN_VIEW))
				{
					String result = packetArr[2];
					
					switch (result)
					{
						case "1":
						{
							String screenList = packetArr[3];
							String listArr[] = screenList.split(","); // 각 상영관 별로 리스트 분할
							
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("`"); // 상영관 별 정보 분할
								String id = infoArr[0];
								String theater_id = infoArr[1];
								String name = infoArr[2];
								String capacity = infoArr[3];
								String row = infoArr[4];
								String col = infoArr[5];
								
								screen_list.add(new ScreenDTO(id, theater_id, name, Integer.valueOf(capacity), Integer.valueOf(row), Integer.valueOf(col)));
							}
							lv_screen.setItems(FXCollections.observableArrayList());
							lv_screen.getItems().addAll(screen_list);
							lv_screen.setOnMouseClicked((MouseEvent) ->
							{
								if (lv_screen.getSelectionModel().getSelectedItem() == null)
									return;
								selectedScreen = lv_screen.getSelectionModel().getSelectedItem();
								lv_screen.setMaxHeight(0);
								lv_screen.getItems().clear();
								tf_screen.setText(selectedScreen.getName());
							});
							lv_screen.setCellFactory(lv -> new ListCell<ScreenDTO>()
							{
								@Override
								protected void updateItem(ScreenDTO item, boolean empty)
								{
									super.updateItem(item, empty);
									setText(item == null ? null : item.getName());
								}
							});
							
							// 필드에 값이 없다면 리스트뷰 감추기
							if (screen_list.size() == 0)
							{
								lv_screen.getItems().clear();
								lv_screen.setMaxHeight(0);
								return;
							}
							lv_screen.setMaxHeight(130);
							// 값이 있으면 리스트뷰 활성화
							break;
						}
						case "2":
						{
							mainGUI.alert("오류", "상영관 리스트가 없습니다.");
							break;
						}
						case "3":
						{
							mainGUI.alert("오류", "상영관 리스트 요청 실패했습니다.");
							break;
						}
					}
					if (result != null)
						return;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@FXML
	void clickedTfTheater(MouseEvent event)
	{
		lv_theater.getItems().clear();
		lv_theater.getItems().addAll(theater_list);
		lv_theater.setMaxHeight(130);
	}
	
	@FXML
	void clickedTfMember(MouseEvent event)
	{
		lv_member.getItems().clear();
		lv_member.getItems().addAll(member_list);
		lv_member.setMaxHeight(130);
	}
	
	@FXML
	void clickedParent(MouseEvent event)
	{
		lv_theater.setMaxHeight(0);
		lv_theater.getItems().clear();
		lv_screen.setMaxHeight(0);
		lv_screen.getItems().clear();
		lv_movie.setMaxHeight(0);
		lv_movie.getItems().clear();
		lv_member.setMaxHeight(0);
		lv_member.getItems().clear();
	}
	
	@FXML
	void typedTfMovie(KeyEvent event)
	{
		// 필드에 값이 없다면 리스트뷰 감추기
		if (tf_movie.getText().equals(""))
		{
			selectedMovie = null;
			lv_movie.getItems().clear();
			lv_movie.setMaxHeight(0);
			return;
		}
		
		// 값이 있으면 리스트뷰 활성화
		lv_movie.setMaxHeight(130);
		lv_movie.setItems(FXCollections.observableArrayList());
		ObservableList<MovieDTO> temp_list = FXCollections.observableArrayList();
		for (int i = 0; i < movie_list.size(); i++)
		{
			// 모든 사용자 리스트에서 현재 입력한 값을 포함하는 요소가 있는지 확인
			if (movie_list.get(i).getTitle().contains(tf_movie.getText()))
			{
				temp_list.add(movie_list.get(i));
			}
		}
		lv_movie.getItems().addAll(temp_list);
	}
	
	@FXML
	void typedTfMember(KeyEvent event)
	{
		// 필드에 값이 없다면 리스트뷰 감추기
		if (tf_member.getText().equals(""))
		{
			selectedMember = null;
			lv_member.getItems().clear();
			lv_member.setMaxHeight(0);
			return;
		}
		
		// 값이 있으면 리스트뷰 활성화
		lv_member.setMaxHeight(130);
		lv_member.setItems(FXCollections.observableArrayList());
		ObservableList<MemberDTO> temp_list = FXCollections.observableArrayList();
		for (int i = 0; i < member_list.size(); i++)
		{
			// 모든 사용자 리스트에서 현재 입력한 값을 포함하는 요소가 있는지 확인
			if (member_list.get(i).getId().contains(tf_member.getText()))
			{
				temp_list.add(member_list.get(i));
			}
		}
		lv_member.getItems().addAll(temp_list);
	}
	
	@FXML
	void searchTimeTable(ActionEvent event)
	{
		initList();
	}
	
	@FXML
	void selectSeat(ActionEvent event)
	{
		try
		{
			if (selectedCustom == null)
			{
				mainGUI.alert("경고", "리스트를 선택해주세요!");
				return;
			}
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(TheaterManage.class.getResource("./xml/user_sub_page/seat_choice.fxml"));
			Parent root = loader.load();
			SeatController controller = loader.<SeatController>getController();
			controller.initData(selectedCustom.getScreenElem(), selectedCustom.getTimeTable());
			stage.setScene(new Scene(root));
			stage.setTitle("좌석 선택");
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(((Node) event.getSource()).getScene().getWindow());
			stage.showAndWait();
			
			row_list = controller.getSelected().get(0);
			col_list = controller.getSelected().get(1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void initList()
	{
		try
		{
			custom_list.clear();
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			
			String mov_id = selectedMovie == null ? "%" : selectedMovie.getId();
			String screen_id = selectedScreen == null ? "%" : selectedScreen.getId();
			String date = dp_start_date.getValue() == null ? "1976-01-01 " : dateFormat.format(dp_start_date.getValue()) + " ";
			String start_time = mb_hours_start.getText().equals("시간") ? "00:00:00.0" : mb_hours_start.getText().replace("시", "") + ":00:00.0";
			String end_time = mb_hours_end.getText().equals("시간") ? "23:59:00.0" : mb_hours_end.getText().replace("시", "") + ":00:00.0";
			String theater_id = "null";
			
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_TIMETABLE_VIEW + "`" + mov_id + "`" + screen_id + "`" + date + "`" + start_time + "`" + end_time + "`" + theater_id);
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("!"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_TIMETABLE_VIEW))
				{
					String result = packetArr[2];
					
					switch (result)
					{
						case "1":
						{
							String screenList = packetArr[3];
							String listArr[] = screenList.split(","); // 각 상영시간표 리스트 분할
							
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("`"); // 상영시간표 별 정보 분할
								String tb_id = infoArr[0];
								String tb_screen_id = infoArr[1];
								String tb_mov_id = infoArr[2];
								String tb_type = infoArr[3];
								String tb_current_rsv = infoArr[4];
								String tb_start_time = infoArr[5];
								String tb_end_time = infoArr[6];
								
								custom_list.add(new CustomDTO(new TimeTableDTO(tb_id, tb_mov_id, tb_screen_id, tb_start_time, tb_end_time, tb_type, Integer.valueOf(tb_current_rsv))));
							}
							break;
						}
						case "2":
						{
							mainGUI.alert("오류", "상영시간표가 없습니다.");
							break;
						}
						case "3":
						{
							mainGUI.alert("오류", "상영시간표 요청 실패했습니다.");
							break;
						}
					}
					if (result != null)
						return;
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
		TheaterDTO theater;
		ScreenDTO screen;
		MovieDTO movie;
		TimeTableDTO timetable;
		
		public CustomDTO(TimeTableDTO timetable) throws Exception
		{
			try
			{
				mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_CUSTOM_INFO + "`1`" + timetable.getId());
				
				while (true)
				{
					String packet = mainGUI.readLine();
					String packetArr[] = packet.split("!"); // 패킷 분할
					String packetType = packetArr[0];
					String packetCode = packetArr[1];
					
					if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_CUSTOM_INFO))
					{
						String result = packetArr[2];
						
						switch (result)
						{
							case "1":
							{
								this.timetable = timetable;
								String infoList = packetArr[3];
								String listArr[] = infoList.split(","); // 각 리스트 분할
								String sc_info[] = listArr[0].split("`"); // 상영관 정보 분할
								String mv_info[] = listArr[1].split("`"); // 영화 정보 분할
								String th_info[] = listArr[2].split("`"); // 영화관 정보 분할
								
								screen = new ScreenDTO(sc_info[0], sc_info[1], sc_info[2], Integer.valueOf(sc_info[3]), Integer.valueOf(sc_info[4]), Integer.valueOf(sc_info[5]));
								movie = new MovieDTO(mv_info[0], mv_info[1], mv_info[2], mv_info[3], mv_info[4], mv_info[5], mv_info[6], mv_info[7], mv_info[8], mv_info[9], Integer.valueOf(mv_info[10]));
								theater = new TheaterDTO(th_info[0], th_info[1], th_info[2], Integer.valueOf(th_info[3]), Integer.valueOf(th_info[4]));
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
		
		public StringProperty getMovie()
		{
			return new SimpleStringProperty(movie.getTitle());
		}
		
		public StringProperty getTheater()
		{
			return new SimpleStringProperty(theater.getName());
		}
		
		public StringProperty getScreen()
		{
			return new SimpleStringProperty(screen.getName());
		}
		
		public StringProperty getStartTime()
		{
			return new SimpleStringProperty(timetable.getStartTime().toString());
		}
		
		public StringProperty getEndTime()
		{
			return new SimpleStringProperty(timetable.getEndTime().toString());
		}
		
		public StringProperty getCurrent()
		{
			return new SimpleStringProperty(timetable.getCurrentRsv() + "`" + screen.getTotalCapacity());
		}
		
		public TimeTableDTO getTimeTable()
		{
			return timetable;
		}
		
		public ScreenDTO getScreenElem()
		{
			return screen;
		}
	}
}
