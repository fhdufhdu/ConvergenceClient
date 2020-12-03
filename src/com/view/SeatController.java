package com.view;

import java.util.ArrayList;
import java.util.Iterator;

import com.db.model.ScreenDTO;
import com.db.model.TimeTableDTO;
import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SeatController
{
    @FXML
    private VBox vbox;
    
    @FXML
    private AnchorPane root;
    
    @FXML
    private Rectangle screen_shape;
    
    @FXML
    private Text screen_text;
    
    @FXML
    private Button btn_select;
    
    @FXML
    private Text selected_seat;
    
    private ToggleButton[][] tb_arr; // 좌석 버튼 배열
    
    private int row; // 행
    private int col; // 열
    
    private boolean is_clicked_close; // 선택완료 버튼을 눌렀는가?
    
    // 초기화
    public void initData(ScreenDTO screen, TimeTableDTO timetable)
    {
        is_clicked_close = false;
        row = screen.getMaxRow();
        col = screen.getMaxCol();
        
        ArrayList<ButtonBar> bar_list = new ArrayList<ButtonBar>();
        tb_arr = new ToggleButton[row + 1][col + 1];
        
        for (int i = 0; i < row + 1; i++)
        {
            for (int j = 0; j < col + 1; j++)
            {
                tb_arr[i][j] = new ToggleButton();
                tb_arr[i][j].setOnAction(new EventHandler<ActionEvent>() // 버튼 눌렀을 시
                {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        Iterator<String> iter = checkSelected().iterator();
                        String result = "선택한 좌석 : ";
                        while (iter.hasNext())
                        {
                            result += iter.next() + " / ";
                        }
                        selected_seat.setText(result);
                    }
                });
                tb_arr[i][j].setStyle("-fx-font-size: 8;");
                tb_arr[i][j].setTextFill(Color.BLACK);
                
                // 몇 행, 몇 열을 표시하기 위한 코드
                if (i == 0 && j == 0)
                {
                    tb_arr[i][j].setDisable(true);
                    tb_arr[i][j].setStyle("-fx-font-size: 8;-fx-background-color: transparent;");
                }
                else if (i == 0 && j != 0)
                {
                    tb_arr[i][j].setDisable(true);
                    tb_arr[i][j].setText(Integer.toString(j));
                    tb_arr[i][j].setStyle("-fx-font-size: 8;-fx-background-color: transparent;");
                }
                else if (i != 0 && j == 0)
                {
                    tb_arr[i][j].setDisable(true);
                    tb_arr[i][j].setText(Character.toString((char) (64 + i)));
                    tb_arr[i][j].setStyle("-fx-font-size: 8;-fx-background-color: transparent;");
                }
                tb_arr[i][j].setMaxSize(10, 10);
            }
        }
        
        try
        {
            // 상영관 좌석 정보 요청
            mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_SEAT_VIEW + "`" + timetable.getId());
            
            while (true)
            {
                String packet = mainGUI.readLine(); // 요청 응답 수신
                String packetArr[] = packet.split("`"); // 패킷 분할
                String packetType = packetArr[0];
                String packetCode = packetArr[1];
                
                if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_SEAT_VIEW))
                {
                    String result = packetArr[2]; // 요청 결과
                    
                    switch (result)
                    {
                        case "1": // 요청 성공 시 좌석 정보 추가
                        {
                            if (packetArr.length > 3)
                            {
                                String rowArr[] = packetArr[3].split("\\|"); // 상영관 열 리스트 분할
                                String colArr[] = packetArr[4].split("\\|"); // 상영관 행 리스트 분할
                                // 예매 되어있는 자리 클릭 비활성화
                                for (int i = 0; i < rowArr.length; i++)
                                {
                                    tb_arr[Integer.valueOf(rowArr[i]) + 1][Integer.valueOf(colArr[i]) + 1].setDisable(true);
                                }
                            }
                            
                            // 버튼배열들 화면에 출력
                            for (int i = 0; i < row + 1; i++)
                            {
                                bar_list.add(new ButtonBar());
                                for (int j = 0; j < col + 1; j++)
                                {
                                    ButtonBar.setButtonData(tb_arr[i][j], ButtonData.APPLY);
                                    bar_list.get(i).getButtons().addAll(tb_arr[i][j]);
                                }
                                bar_list.get(i).setMinSize(0, 0);
                                bar_list.get(i).setPrefSize(25, 20);
                                bar_list.get(i).setMaxSize(25, 20);
                                bar_list.get(i).setButtonMinWidth(25);
                            }
                            vbox.getChildren().addAll(bar_list);
                            vbox.setAlignment(Pos.CENTER);
                            vbox.setSpacing(10);
                            
                            // 크기 조절
                            root.setPrefSize((col + 1) * 36, (row + 1) * 36 + 180);
                            vbox.setPrefSize((col + 1) * 35, (row + 1) * 35);
                            double root_width;
                            
                            // 위치 조절
                            if (root.getPrefWidth() < root.getMinWidth())
                            {
                                root_width = root.getMinWidth();
                                vbox.setLayoutX(-30);
                            }
                            else
                            {
                                root_width = root.getPrefWidth();
                                vbox.setLayoutX(-10);
                            }
                            screen_shape.setLayoutX(root_width / 2 - screen_shape.getWidth() / 2);
                            screen_text.setLayoutX(root_width / 2 - screen_text.getWrappingWidth() / 2);
                            btn_select.setLayoutX(root_width - 90);
                            return;
                        }
                        case "2": // 요청 실패
                        {
                            mainGUI.alert("오류", "좌석 정보를 불러오는데 실패했습니다.");
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
    
    // 선택완료 버튼 클릭 시
    @FXML
    void close(ActionEvent event)
    {
        is_clicked_close = true;
        Stage stage = (Stage) btn_select.getScene().getWindow();
        stage.close();
    }
    
    // 선택한 버튼 좌표를 리스트로 획득하기
    private ArrayList<String> checkSelected()
    {
        ArrayList<String> result_list = new ArrayList<String>();
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                if (tb_arr[i][j].isSelected())
                {
                    result_list.add(Character.toString((char) (64 + i)) + Integer.toString(j));
                }
            }
        }
        return result_list;
    }
    
    // 선택한 버튼 좌표를 획득하기
    public ArrayList<ArrayList<Integer>> getSelected()
    {
        ArrayList<ArrayList<Integer>> seat_list = new ArrayList<ArrayList<Integer>>();
        seat_list.add(new ArrayList<Integer>());
        seat_list.add(new ArrayList<Integer>());
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                if (tb_arr[i][j].isSelected())
                {
                    seat_list.get(0).add(i - 1);
                    seat_list.get(1).add(j - 1);
                }
            }
        }
        return seat_list;
    }
    
    // 선택완료 버튼을 눌렀는지 확인
    public boolean getIsClickedClose()
    {
        return is_clicked_close;
    }
}
