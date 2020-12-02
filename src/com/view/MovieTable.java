package com.view;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;

import com.db.model.MovieDTO;
import com.db.model.ScreenDTO;
import com.db.model.TheaterDTO;
import com.db.model.TimeTableDTO;
import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MovieTable implements Initializable
{
	// 테이블 뷰를 위한 리스트
	private ObservableList<TheaterDTO> theater_list;
	private ObservableList<MovieDTO> movie_list;
	private ObservableList<CustomDTO> custom_list;
	
	// 테이블 뷰에서 선택된 객체
	private TheaterDTO selectedThea;
	private MovieDTO selectedMovie;
	private CustomDTO selectedCustom;
	
	// 좌석 좌표 객체
	private ArrayList<Integer> row_list;
	private ArrayList<Integer> col_list;
	
	@FXML
	private BorderPane bp_parent;
	
	@FXML
	private TableView<TheaterDTO> tv_theater;
	
	@FXML
	private TableColumn<TheaterDTO, String> tc_theater;
	
	@FXML
	private TableView<MovieDTO> tv_movie;
	
	@FXML
	private TableColumn<MovieDTO, String> tc_movie;
	
	@FXML
	private DatePicker dp_date;
	
	@FXML
	private VBox vbox;
	
	@FXML
	private Text t_movie_title;
	
	@FXML
	private Button btn_reservation;
	
	// 초기화
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		try
		{
			// 리스트 초기화
			initTheaterList();
			initMovieList();
			dp_date.setValue(LocalDate.now());
			dp_date.valueProperty().addListener(new ChangeListener<LocalDate>() // datepicker 선택시 발생하는 이벤트
			{
				@Override
				public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue)
				{
					try
					{
						// 예매 버튼 생성
						setRsvButton();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
				}
			});
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// 영화관 리스트 초기화
	private void initTheaterList() throws Exception
	{
		try
		{
			theater_list = FXCollections.observableArrayList();
			
			// 사용자 -> 영화관 리스트 요청
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_THEATER_VIEW);
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_THEATER_VIEW))
				{
					String result = packetArr[2]; // 요청 결과
					
					switch (result)
					{
						case "1": // 요청 성공
						{
							String theaterList = packetArr[3];
							String listArr[] = theaterList.split("\\{"); // 각 영화관 별로 분할
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
							// 테이블 뷰 초기화
							tv_theater.getItems().clear();
							
							// 각 테이블뷰 컬럼에 어떠한 값이 들어갈 것인지 세팅
							tc_theater.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
							
							// 테이블 뷰와 리스트를 연결
							tv_theater.setItems(theater_list);
							
							// 테이블 뷰 row 선택 시 발생하는 이벤트 지정
							tv_theater.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TheaterDTO>()
							{
								@Override
								public void changed(ObservableValue<? extends TheaterDTO> observable, TheaterDTO oldValue, TheaterDTO newValue)
								{
									selectedThea = tv_theater.getSelectionModel().getSelectedItem();
									try
									{
										setRsvButton();
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}
								}
							});
							return;
						}
						case "2": // 영화관 리스트 비어있음
						{
							return;
						}
						case "3": // 요청 실패
						{
							mainGUI.alert("오류", "영화관 리스트 요청 실패했습니다");
							return;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			mainGUI.alert("오류", "영화관 리스트 요청 실패했습니다");
			e.printStackTrace();
		}
	}
	
	// 영화 리스트 초기화
	private void initMovieList() throws Exception
	{
		try
		{
			movie_list = FXCollections.observableArrayList();
			
			// 사용자 -> 영화 리스트 요청
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_MOVIE_VIEW + "`%`1976-01-01`2222-01-01`%`%`%`1");
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_MOVIE_VIEW))
				{
					String result = packetArr[2]; // 요청 결과
					
					switch (result)
					{
						case "1": // 요청 성공
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
								
								movie_list.add(new MovieDTO(mv_id, mv_title, mv_release_date, mv_is_current, mv_plot, mv_poster_path, mv_stillCut_path, mv_trailer_path, mv_director, mv_actor, mv_min));
							}
							// 테이블 뷰 초기화
							tv_movie.getItems().clear();
							
							// 각 테이블뷰 컬럼에 어떠한 값이 들어갈 것인지 세팅
							tc_movie.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
							
							// 테이블 뷰와 리스트를 연결
							tv_movie.setItems(movie_list);
							
							// 테이블 뷰 row 선택 시 발생하는 이벤트 지정
							tv_movie.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MovieDTO>()
							{
								@Override
								public void changed(ObservableValue<? extends MovieDTO> observable, MovieDTO oldValue, MovieDTO newValue)
								{
									try
									{
										if (selectedThea == null)
										{
											mainGUI.alert("경고", "영화관을 선택해주세요");
											return;
										}
										selectedMovie = tv_movie.getSelectionModel().getSelectedItem();
										
										setRsvButton();
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}
								}
							});
							return;
						}
						case "2": // 영화 리스트 비어있음
						{
							return;
						}
						case "3": // 요청 실패
						{
							mainGUI.alert("영화 리스트", "영화 리스트 요청 실패했습니다.");
							return;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			mainGUI.alert("영화 리스트", "영화 리스트 요청 실패했습니다.");
			e.printStackTrace();
		}
		
	}
	
	//
	private void initCustomList() throws Exception
	{
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		try
		{
			String mov_id = selectedMovie.getId();
			String screen_id = "%";
			String date = dp_date.getValue().format(format);
			String start_time = " 00:00:00.0";
			String end_time = " 23:59:00.0";
			String theater_id = selectedThea.getId();
			
			custom_list = FXCollections.observableArrayList();
			
			// 사용자 -> 선택한 영화에 해당하는 상영시간표 요청
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_TIMETABLE_VIEW + "`" + mov_id + "`" + screen_id + "`" + date + "`" + start_time + "`" + end_time + "`" + theater_id);
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_TIMETABLE_VIEW))
				{
					String result = packetArr[2]; // 요청 결과
					
					switch (result)
					{
						case "1": // 요청 성공
						{
							String screenList = packetArr[3];
							String listArr[] = screenList.split("\\{"); // 각 상영시간표 리스트 분할
							
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("\\|"); // 상영시간표 별 정보 분할
								String tb_id = infoArr[0];
								String tb_screen_id = infoArr[1];
								String tb_mov_id = infoArr[2];
								String tb_type = infoArr[3];
								String tb_current_rsv = infoArr[4];
								String tb_start_time = infoArr[5];
								String tb_end_time = infoArr[6];
								
								TimeTableDTO temp = new TimeTableDTO(tb_id, tb_mov_id, tb_screen_id, tb_start_time, tb_end_time, tb_type, Integer.valueOf(tb_current_rsv));
								if (temp.getStartTime().after(new Timestamp(System.currentTimeMillis())))
									custom_list.add(new CustomDTO(temp));
							}
							return;
						}
						case "2": // 상영시간표가 없음
						{
							t_movie_title.setText("<해당하는 상영시간표가 없습니다>");
							Node temp = vbox.getChildren().get(0);
							vbox.getChildren().clear();
							vbox.getChildren().add(temp);
							return;
						}
						case "3": // 요청 실패
						{
							mainGUI.alert("오류", "상영시간표 요청 실패했습니다.");
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
	
	// 예매 버튼 세팅
	private void setRsvButton() throws Exception
	{
		initCustomList();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		
		int cnt = custom_list.size();
		if (cnt <= 0)
		{
			// vbox 재조정
			t_movie_title.setText("<해당하는 상영시간표가 없습니다>");
			Node temp = vbox.getChildren().get(0);
			vbox.getChildren().clear();
			vbox.getChildren().add(temp);
			return;
		}
		t_movie_title.setText("<" + selectedMovie.getTitle() + ">");
		ArrayList<ButtonBar> bar_list = new ArrayList<ButtonBar>();
		
		for (int i = 0; i < (cnt < 4 ? 1 : cnt / 4); i++)
		{
			bar_list.add(new ButtonBar());
			for (int j = 0; j < 4; j++)
			{
				if ((i + 1) * (j + 1) > cnt)
					break;
				selectedCustom = custom_list.get(((i + 1) * (j + 1) - 1));
				Button btn = new Button(selectedCustom.getScreen().getName() + "관\n" + selectedCustom.getTimeTable().getCurrentRsv() + "/" + selectedCustom.getScreen().getTotalCapacity() + "\n" + format.format(new Date(selectedCustom.getTimeTable().getStartTime().getTime())));
				btn.setOnAction(new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						selectSeat();
					}
				});
				ButtonBar.setButtonData(btn, ButtonData.LEFT);
				bar_list.get(i).getButtons().addAll(btn);
			}
		}
		
		// vbox 재조정, 예매 버튼 날리고 새로 추가
		Node temp = vbox.getChildren().get(0);
		vbox.getChildren().clear();
		vbox.getChildren().add(temp);
		vbox.getChildren().addAll(bar_list);
		vbox.setSpacing(10);
	}
	
	// 좌석 선택
	private void selectSeat()
	{
		try
		{
			// 새로운 페이지 생성
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(MovieTable.class.getResource("./xml/user_sub_page/seat_choice.fxml"));
			Parent root = loader.load();
			SeatController controller = loader.<SeatController>getController();
			controller.initData(selectedCustom.getScreen(), selectedCustom.getTimeTable());
			stage.setScene(new Scene(root));
			stage.setTitle("좌석 선택");
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(bp_parent.getScene().getWindow());
			stage.showAndWait();
			
			if (!controller.getIsClickedClose()) // 만약 선택완료를 누르지 않고 종료시
				return;
			
			row_list = controller.getSelected().get(0);
			col_list = controller.getSelected().get(1);
			
			if (row_list.size() == 0 || col_list.size() == 0) // 선택한 좌석이 0개일 경우
				return;
			
			String user_id = Login.USER_ID;
			String timetable_id = selectedCustom.getTimeTable().getId();
			String rowList = "";
			String colList = "";
			
			Iterator<Integer> riter = row_list.iterator();
			Iterator<Integer> citer = col_list.iterator();
			
			while (riter.hasNext())
				rowList += Integer.toString(riter.next()) + "|"; // 선택한 row별로 |로 구분
				
			while (citer.hasNext())
				colList += Integer.toString(citer.next()) + "|"; // 선택한 col별로 |로 구분
				
			// 사용자 -> 선택한 좌석으로 예매 요청
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_RESERVATION_ADD + "`" + user_id + "`" + timetable_id + "`" + rowList + "`" + colList);
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_RESERVATION_ADD))
				{
					String result = packetArr[2]; // 요청 결과
					
					switch (result)
					{
						case "1": // 요청 성공
						{
							int price = Integer.valueOf(packetArr[3]);
							
							startPayment(price);
							setRsvButton();
							return;
						}
						case "2": // 요청 실패
						{
							mainGUI.alert("예매", "예매에 실패했습니다.");
							setRsvButton();
							return;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			mainGUI.alert("오류", "예매에 실패했습니다.");
			e.printStackTrace();
		}
	}
	
	// 결제 진행
	private void startPayment(int price)
	{
		try
		{
			// 결제창 띄움
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(MovieTable.class.getResource("./xml/user_sub_page/payment.fxml"));
			Parent root = loader.load();
			Payment controller = loader.<Payment>getController();
			ArrayList<ArrayList<Integer>> seat_list = new ArrayList<ArrayList<Integer>>();
			seat_list.add(row_list);
			seat_list.add(col_list);
			controller.initData(selectedThea, selectedCustom.getScreen(), selectedMovie, selectedCustom.getTimeTable(), seat_list, price);
			stage.setScene(new Scene(root));
			stage.setTitle("좌석 선택");
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(bp_parent.getScene().getWindow());
			stage.showAndWait();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private class CustomDTO
	{
		ScreenDTO screen;
		TimeTableDTO timetable;
		
		public CustomDTO(TimeTableDTO timetable)
		{
			try
			{
				// 해당 상영시간표를 가지는 상영관 정보 요청
				mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_CUSTOM_INFO + "`0`" + timetable.getId());
				
				while (true)
				{
					String packet = mainGUI.readLine(); // 요청 응답 수신
					String packetArr[] = packet.split("`"); // 패킷 분할
					String packetType = packetArr[0];
					String packetCode = packetArr[1];
					
					if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_CUSTOM_INFO))
					{
						String result = packetArr[2]; // 요청 결과
						
						switch (result)
						{
							case "1": // 요청 성공
							{
								this.timetable = timetable;
								String infoList = packetArr[3];
								String sc_info[] = infoList.split("\\|"); // 상영관 정보 분할
								screen = new ScreenDTO(sc_info[0], sc_info[1], sc_info[2], Integer.valueOf(sc_info[3]), Integer.valueOf(sc_info[4]), Integer.valueOf(sc_info[5]));
								return;
							}
							case "2": // 요청 실패
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
		
		public ScreenDTO getScreen()
		{
			return screen;
		}
		
		public TimeTableDTO getTimeTable()
		{
			return timetable;
		}
	}
	
	public void initData(MovieDTO movie)
	{
		try
		{
			initTheaterList();
			initMovieList();
			
			tv_theater.getSelectionModel().select(0);
			for (int i = 0; i < movie_list.size(); i++)
			{
				if (movie_list.get(i).getId().equals(movie.getId()))
				{
					tv_movie.getSelectionModel().select(i);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
