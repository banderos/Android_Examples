package com.testtable.myapp;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.widget.RelativeLayout.*;
import android.text.*;
import android.widget.TableRow.LayoutParams;
import android.graphics.*;
import android.view.View.*;
import android.graphics.drawable.*;
import android.content.res.*;
import android.R.*;
import java.util.*;
import java.io.File; 
import java.util.Date; 
import jxl.Workbook;
import jxl.Sheet;
import jxl.Cell;
import jxl.format.CellFormat;
import jxl.format.Font;
import jxl.CellType;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.DateCell;
import jxl.write.*;
import jxl.read.*;
import java.io.*;
import jxl.read.biff.*;
import jxl.biff.*;
import android.widget.AdapterView.*;
import org.w3c.dom.*;
import android.opengl.*;
import com.ui.collections.activities.*;

import com.data.utils.DateTime;
import java.text.*;
import com.data.utils.*;
import android.util.*;
import jxl.*;
import jxl.biff.formula.*;
/**
 *
 */
public class MainActivity extends Activity
{
	protected static final String TAG = "ExcelSheetBook";
	//отдельный идентификатор для контрола редактирования значения ячейки
    private static final int etId = 0x7f0d0100 ;
	private static final class CellTypes
	{
		public static final String DATE = "DateCell";


	}
	private Context ctx = null;
    private Resources res = null;
    private String currentSheet = null;
    private float touchedX;
    private float touchedY;
    private int cellHeight, cellWidth;
    private boolean longClicked = false;
    private boolean fileSelected = false;
	private int background = Color.BLUE, foreground = Color.YELLOW;

    // определение одного requestCode мы используем для получения результата
	static final private int GET_CODE = 0;

    private String maskFile;
    private String currentPath;
	Grafics currentHandler = null;
	HashMap<String, Drawable> discretColorPallite = new HashMap<String, Drawable>();
    // карта с текстовыми полями для быстрого доступа по адресу в формате электронной таблицы
    HashMap<String,ArrayList<TextView>> texts = new HashMap<String,ArrayList<TextView>>();
    // структура данных документа в виде массива листов
    HashMap<String,Object> allSheets = new HashMap<String, Object>();
	// кнопки для работы с книгой
    private Button buttonClicked = null;
    private Button bAddRow;
	private Button bAddSheet;
    private Button bOpen;
    private Button bSave;
    private Button bSaveAs;
	private Button bClear;

	// выбор листа для отображения
    private Spinner spnSheetSelector;
    private RelativeLayout mainLayout;
	private HorizontalScrollView scV;
    private TableLayout table;

    private ArrayAdapter spnAdapter;

    private ArrayList<TableRow> tableRows = new ArrayList<TableRow>();
	private EditText txtNameSheet;
    private EditText et;
    private View hidingView;
    private TextView txtFilePath;

	private PopupMenu popup = null;
    private MenuItem mnuDelete = null;
    private MenuItem mnuAdd = null;
    private MenuItem mnuRename = null;

	private int operationMode = OperationMode.NOTHING;;
    public static final class OperationMode
	{
        public static final int NOTHING = -1;
        public static final int CREATE_SHEET = 0;
        public static final int RENAME_SHEET = 1;
        public static final int DELETE = 3;
    }
    /**<h2>Описание</h2>
     * <p>Обработчик клика по таблице и кнопкам интерфейса</p>
     *
     */
	private View.OnClickListener clickListener = new View.OnClickListener(){
		@Override
		public void onClick(View p1)
		{
            if (p1.getClass().toString().contains("Button"))
                buttonClicked = (Button) p1;
            if (!longClicked)
            {
                int idView = p1.getId();
                switch (idView)
                {
                    case R.id.main:

                        break;
                    case R.id.bAddRow:

                        break;
                    case R.id.bAddSheet:

                        break;
                    case R.id.bClear:

                        break;
                    case R.id.bSave:

                        break;

                }
                if (!fileSelected)
				{
                    // выбираем файл для выполнения операции с ним
                    switch (idView)
                    {
                        case R.id.bSaveAs:
                        case R.id.bOpen:
                            maskFile = ".xls, .amd";
                            currentPath = txtFilePath.getText().toString();
                            Intent intentBrowse = new Intent() ;
                            intentBrowse.putExtra("file", currentPath);
                            intentBrowse.putExtra("mask", maskFile);
                            Bundle typeIcons = new Bundle();
                            Grafics gr = new Grafics();
                            typeIcons.putIntArray("amd", gr.bitmap2arrInt(
                                 BitmapFactory.decodeResource(ctx.getResources(), R.drawable.amd)));
                            intentBrowse.putExtra("typeIcons", typeIcons);
                            intentBrowse.setClass(ctx, FileManager.class);
                            startActivityForResult(intentBrowse, GET_CODE);
                            break;
                        default:
                            ((TextView)p1).setTextColor(foreground);

                    }
	        	}
                else
                {
                    File operationFile = new File(txtFilePath.getText().toString());
                    if (!operationFile.isDirectory())
					{
						// выполныем ввод вывод
                        switch (idView)
                        {
                            case R.id.bOpen:
								WorkbookSettings ws = new WorkbookSettings();

								ws.setLocale(new Locale("en_EN"));
                                Workbook workbook=null;
                                try
                                {
                                    workbook = Workbook.getWorkbook(operationFile, ws);
                                }
                                catch (IOException e)
                                {
									e.printStackTrace();
								}
                                catch (BiffException e)
                                {
									e.printStackTrace();
								}
								if (workbook != null)
								{
									// читаем и выводим таблицу Excel
									Sheet[] sheets = workbook.getSheets();
									HashMap<String, Object> sheetContent = new HashMap<String, Object>();
									for (int d = 0; d < sheets.length;d++)
									{
										Sheet sheet = workbook.getSheet(d);
										sheetContent.clear();
										int columnsSheet = sheet.getColumns();
										int rowsSheet = sheet.getRows();
										int maxRows = 0, maxColumns = 0;
										for (int r = 0; r < rowsSheet; r++)
										{
											for (int c = 0;c < columnsSheet; c++)
											{
												String text_value = "";
												Cell currentCell = sheet.getCell(c, r);
												HashMap<String, Object> cellContent = new HashMap<String, Object>();
												CellFormat formatCell = currentCell.getCellFormat();
												CellType ct = currentCell.getType();
												if (formatCell != null)
												{
													cellContent.put("type", ct);
													cellContent.put("alignment", formatCell.getAlignment().getDescription()
																	+ "_" + formatCell.getVerticalAlignment().getDescription());
													cellContent.put("wrap", formatCell.getWrap());
													cellContent.put("background", formatCell.getBackgroundColour().getDescription());
													cellContent.put("border_style", formatCell.getBorder(Border.LEFT).getDescription() + "_"
																	+ formatCell.getBorder(Border.TOP).getDescription() + "_"
																	+ formatCell.getBorder(Border.RIGHT).getDescription() + "_"
																	+ formatCell.getBorder(Border.BOTTOM).getDescription());
													cellContent.put("border", formatCell.getBorderColour(Border.LEFT).getDescription() + "_"
																	+ formatCell.getBorderColour(Border.TOP).getDescription() + "_"
																	+ formatCell.getBorderColour(Border.RIGHT).getDescription() + "_"
																	+ formatCell.getBorderColour(Border.BOTTOM).getDescription());
													cellContent.put("orientation", formatCell.getOrientation().getDescription());
													cellContent.put("pattern", formatCell.getPattern().getDescription());

													Font fontCell = formatCell.getFont();
													cellContent.put("font", fontCell.getName() 
																	+ "_" + fontCell.getPointSize() 
																	+ "_" + String.valueOf(fontCell.getBoldWeight()) 
																	+ "_" + fontCell.getUnderlineStyle().getDescription()
																	+ "_" + fontCell.getScriptStyle().getDescription()
																	+ "_" + fontCell.isStruckout()
																	+ "_" + fontCell.isItalic());
													cellContent.put("format_string", formatCell.getFormat().getFormatString());
												}
												if (ct == CellType.LABEL)
												{ 
													LabelCell lc = (LabelCell) currentCell;
													cellContent.put("value", lc.getString()); 
													text_value = lc.getString();
												} 

												if (ct == CellType.NUMBER)
												{ 
													NumberCell nc = (NumberCell) currentCell; 
													cellContent.put("type", CellType.NUMBER);
													cellContent.put("value", nc.getValue()); 
													text_value = String.valueOf(nc.getValue());
												} 

												if (ct == CellType.DATE)
												{ 
													DateCell dc = (DateCell) currentCell; 
													cellContent.put("value",  dc.getDate()); 
													text_value = String.valueOf(dc.getDate());
												}
												if (ct == CellType.NUMBER_FORMULA
													|| ct == CellType.DATE_FORMULA
													|| ct == CellType.STRING_FORMULA
													|| ct == CellType.BOOLEAN_FORMULA
													|| ct == CellType.FORMULA_ERROR)
												{
													FormulaCell fc = (FormulaCell) currentCell;
													String fv = null;
													try
													{	
														if (ct == CellType.NUMBER_FORMULA)
														{
															fv = String.valueOf(((NumberFormulaCell) fc).getValue());
														}
														if (ct == CellType.DATE_FORMULA)
														{
															fv = String.valueOf(((DateFormulaCell) fc).getDate());
														}
														if (ct == CellType.STRING_FORMULA)
														{
															fv = String.valueOf(((StringFormulaCell)fc).getString());
														}
														if (ct == CellType.BOOLEAN_FORMULA)
														{
															fv = String.valueOf(((BooleanFormulaCell) fc).getValue());
														}
														if (ct == CellType.FORMULA_ERROR)
														{
															fv = String.valueOf(((ErrorFormulaCell)fc).getErrorCode());
														}
														cellContent.put("formula",  fc.getFormula());
														cellContent.put("value", fv);
													}
													catch (FormulaException e)
													{
														e.printStackTrace();
													} 

												}
												if (currentCell.getType() == CellType.EMPTY)
												{

												}
												else
												{
													// проверяем максимальное ли значение имеет ряд или колонка и правим значение
													// если стало больше
													maxRows = Math.max(maxRows, r);
													maxColumns = Math.max(maxColumns, c);
												}
												/*
												 sheetContent.put(
												 enumAlfa(c, "ABCDEFGHIJKLMNOPQRSTUVWXYZ")
												 + String.valueOf(r),new HashMap(cellContent));
												 */
												if (text_value != "")
												{
													sheetContent.put(String.valueOf(c) + "_"
																	 + String.valueOf(r), cellContent);
												}
											}
										}
										sheetContent.put("size", String.valueOf(maxColumns) + "x" + String.valueOf(maxRows));
										allSheets.put(sheet.getName(), new HashMap<String,Object>(sheetContent));
									}//else{
									// выводим сообщение о пустой книге или создаем новую 
									// и связываем с выбранным именем файла

									//}
                                }

                                break;
                            case R.id.bSave:
                            case R.id.bSaveAs:
                                WritableWorkbook wWorkbook = null;
                                try
                                {
                                    wWorkbook = Workbook.createWorkbook(new File(txtFilePath.getText().toString()));
                                    // перебираем все листы
                                    Iterator iterSheets = allSheets.keySet().iterator();
                                    while (iterSheets.hasNext())
                                    {
                                        String keySheet = iterSheets.next().toString();
                                        jxl.write.WritableSheet sheet = wWorkbook.createSheet(
                                            keySheet, 0);
                                        HashMap shtContent = (HashMap) allSheets.get(keySheet);
                                        Iterator iterCnt = shtContent.keySet().iterator();
                                        while (iterCnt.hasNext())
                                        {
                                            String keyCnt = iterCnt.next().toString();
                                            String[] address = keyCnt.split("_");
                                            HashMap cnt = (HashMap) shtContent.get(keyCnt);
                                            CellType typeCell = (CellType) cnt.get("type");
                                            int c = Integer.valueOf(address[0]),  
                                                r = Integer.valueOf(address[1]);
                                            Object val = cnt.containsKey("value") ?cnt.get("value"): null;
                                            if (val != null)
                                            {
                                                if (typeCell == CellType.LABEL)
                                                {
                                                    Label lc = new Label(c, r, (String)val);
                                                    sheet.addCell(lc);
                                                }
                                                if (typeCell == CellType.NUMBER)
                                                {
                                                    jxl.write.Number nc = new jxl.write.Number(c, r, val);
                                                    sheet.addCell(nc);
                                                }
                                                if (typeCell == CellType.DATE)
                                                { 
                                                    jxl.write.DateTime dc = new jxl.write.DateTime(c, r, (Date)val);
                                                    sheet.addCell(dc);

                                                }
												if (typeCell == CellType.EMPTY)
												{

												}
												if (typeCell == CellType.BOOLEAN_FORMULA
													|| typeCell == CellType.STRING_FORMULA
													|| typeCell == CellType.NUMBER_FORMULA
													|| typeCell == CellType.DATE_FORMULA
													|| typeCell == CellType.FORMULA_ERROR)
												{
													if (typeCell == CellType.BOOLEAN_FORMULA)
													{

													}
													if (typeCell == CellType.STRING_FORMULA)
													{

													}
													if (typeCell == CellType.NUMBER_FORMULA)
													{

													}
													if (typeCell == CellType.DATE_FORMULA)
													{

													}
													if (typeCell == CellType.FORMULA_ERROR)
													{

													}
													String formula = (String) cnt.get("formula");
													jxl.write.Formula f = new jxl.write.Formula(c, r, formula);
													sheet.addCell(f);
												}
                                            }
                                            else
                                            {
                                                if (typeCell == CellType.LABEL)
                                                {
                                                    Label lc = new Label(c, r, "");
                                                    sheet.addCell(lc);
                                                }
                                            }
                                        }
                                    }


                                    wWorkbook.write();
                                    wWorkbook.close();

                                }
                                catch (IOException e)
                                {
									e.printStackTrace();
								}
                                catch (WriteException e)
                                {
									e.printStackTrace();
								}
                                break;
                        }
                    }
                }
                // сбрасываем настройку, если ввод/вывод выполнен
                setSpinner();
                fileSelected = false;
            }
            else
			{
                longClicked = false;
            }
		}
	};
    /**<h2>Описание</h2>
     * <p>Вызывается, когда вызванный актив возвращает управление вызвавшему</p>
     * @param requestCode - 
     * @param resultCode - результирующий код
     * @param data - данные возвращаемые в результате
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (requestCode == GET_CODE)
        {
            if (resultCode == RESULT_OK)
            {
				txtFilePath.setText(data.getAction().toString());
                fileSelected = true;
                buttonClicked.performClick();
            }
        }       
    }
	/**
	 *<h2>Описание</h2>
	 * <p></p>
	 */
    private AdapterView.OnItemSelectedListener spnOnSelectedItem = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            currentSheet = spnSheetSelector.getSelectedItem().toString();
            renderTable(currentSheet);
        }

        @Override
        public void onNothingSelected(AdapterView<?> p1)
        {
            renderTable(currentSheet);
        }
    };

    private View.OnLongClickListener longClickListener = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View v)
        {

            et.setX(touchedX);
            et.setY(touchedY);
            hidingView =  v;
            hidingView.setVisibility(View.GONE);
            et.setVisibility(View.VISIBLE);
            et.setText(((TextView)hidingView).getText());
            longClicked = true;
            return false;
        }


    };

    private View.OnTouchListener touchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            touchedX = v.getX();
            float tmp = table.getY();
            touchedY = event.getRawY() + cellHeight - ((View)table.getParent().getParent()).getY();
            cellHeight = v.getHeight();
            cellWidth = v.getWidth();
            et.setHeight(cellHeight);
            et.setWidth(cellWidth);

            return false;
        }


    };

    private View.OnKeyListener onKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keycode, KeyEvent event)
        {
            int id = v.getId();
            switch (id)
            {
                case etId:
                    // восстанквливаем видимость скрытой ячейки
                    switch (keycode)
                    {
                        case KeyEvent.KEYCODE_ENTER:
                        case KeyEvent.KEYCODE_ESCAPE:
                        case KeyEvent.KEYCODE_BACK:
                            hidingView.setVisibility(View.VISIBLE);
                            et.setVisibility(View.GONE);
                            break;
                    }
                    // сохраняем введенные данные в ячейку или отменяем ввод
                    switch (keycode)
                    {
                        case KeyEvent.KEYCODE_ENTER:
                        case KeyEvent.KEYCODE_BACK:
                            ((TextView)hidingView).setText(et.getText());
                            break;
                        case KeyEvent.KEYCODE_ESCAPE:

                            break;
                    }
                    break;
            }
            return false;
        }
    };


    /**<h2>Описание</h2>
     * <p>Прорисовка таблицы</p>
     * @param name - название листа, подлежащего отображению
     */
    private void renderTable(String name)
    {
        if (name != null)
            if (name.length() > 0)
            {
                HashMap<String,?> currentSheetContent =(HashMap<String, ?>) allSheets.get(name);
                String[] sizeSheet = ConvertTypes.stringToArray(currentSheetContent.get("size").toString(), "x");

                int[] sizes = new int[]{Integer.valueOf(sizeSheet[0]), Integer.valueOf(sizeSheet[1])};

                table.removeAllViews();
                et = new EditText(ctx);
                mainLayout.addView(et);
                et.setY(table.getY());
                et.setX(table.getX());
                et.setVisibility(View.GONE);
                et.setOnKeyListener(onKeyListener);
                et.setId(etId);
                et.setMaxLines(1);
                //      for (int j = 0;j < sizes[0]; j++)
                // растягиваем колонку пропорционально со всеми остальными, имеющими 
                // тот же атрибут
                //  table.setColumnStretchable(j, true);
                // сворачиваем вторую колонку в таблице
                //   table.setColumnCollapsed(2, true);


                for (int r = 0;r < sizes[1];r++)
                {
                    TableRow tr = new TableRow(ctx);
                    tr.setDividerPadding(5);
                    tr.setMinimumHeight(26);
					tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT
																  , ViewGroup.LayoutParams.WRAP_CONTENT));
//            tableRows[i].setDividerDrawable(res.getDrawable(R.drawable.ic_launcher));
                    tr.invalidate();

                    tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT
                                                        , LayoutParams.WRAP_CONTENT));
                    for (int c = 0; c < sizes[0];c++)
                    {
                        String key = String.valueOf(c) + "_" + String.valueOf(r);
						String value = "", prefix = "", suffix = "", backgroundCell = "", font[]= null, alignment[] = null,border[] = null,border_style[] = null;
						String colourCell[];
						boolean isBold = false, isItalic = false, isUnderline = false, isStruck = false;
						HashMap tmp = (HashMap) currentSheetContent.get(key);
                        if (tmp != null)
						{
							font = ConvertTypes.stringToArray(tmp.containsKey("font") ?tmp.get("font").toString(): "null", "_");
							alignment = ConvertTypes.stringToArray(tmp.containsKey("alignment") ?tmp.get("alignment").toString(): "null", "_");
							border = ConvertTypes.stringToArray(tmp.containsKey("border") ?tmp.get("border").toString(): "null", "_");
							border_style = ConvertTypes.stringToArray(tmp.containsKey("border_style") ?tmp.get("border_style").toString(): "null", "_");
                            value = tmp.containsKey("value") ?tmp.get("value").toString(): "";
							backgroundCell = tmp.containsKey("background") ?tmp.get("background").toString(): "";
						}
                        TextView text = new TextView(ctx);

                        text.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT
                                                              , LayoutParams.MATCH_PARENT));

						text.setBackground(discretColorPallite.get(backgroundCell));
						// формат для цвета текста
						String[] formatColorFont = new String[]{"<font color='#%s'>","</font>"};
						if (font != null)
						{
							if (font.length > 1)
							{
								float s = Integer.valueOf(font[1]);
								text.setTextSize(s * 2);
								switch (Integer.valueOf(font[2]))
								{
									case 400:
										isBold = false;
										break;
									case 700:
										isBold = true;
										break;
									default:
										// сообщаем о другом значении жирности шрифта
										Log.i(TAG, "Unknown value for boldweight");
								}
								if (font[5].contains("true"))
								{
									isStruck = true;
								}
								if (font[6].contains("true"))
								{
									isItalic = true;
								}
								if (!font[3].contains("none"))
								{
									isUnderline = true;
								}
								// формируем начальные и конечные теги html
								if (isBold)
								{
									prefix = prefix + "<b>";
									suffix =  "</b>" + suffix;
								}
								if (isItalic)
								{
									prefix = prefix + "<i>";
									suffix = "</i>" + suffix;
								}
								if (isStruck)
								{
									prefix = prefix + "<s>";
									suffix = "</s>" + suffix;
								}
								if (isUnderline)
								{
									prefix = prefix + "<u>";
									suffix = "</u>" + suffix;
								}
							}
						}

                        text.setOnClickListener(clickListener);
                        text.setOnLongClickListener(longClickListener);
                        text.setOnTouchListener(touchListener);


                        text.setText(android.text.Html.fromHtml(prefix + value + suffix));
                        tr.addView(text);

                    }
                    tableRows.add(tr);
                    table.addView(tr);
                }
            }
    }
    /**
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        res = getResources();
        setContentView(R.layout.main);

        spnSheetSelector = (Spinner) findViewById(R.id.spnSheetSelector);


		mainLayout = (RelativeLayout) findViewById(R.id.main);
        scV = (HorizontalScrollView) findViewById(R.id.scroll_nor);
        table = new TableLayout(ctx);
        table.setOnTouchListener(touchListener);
        table.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT
                                               , LayoutParams.WRAP_CONTENT));
        scV.addView(table);

        //scV.setPadding(0, 120, 0, 0);
        //       mainLayout.addView(scV);

        bSave = (Button) findViewById(R.id.bSave);
        bSaveAs = (Button) findViewById(R.id.bSaveAs);
        bOpen = (Button) findViewById(R.id.bOpen);
		bAddRow = (Button) findViewById(R.id.bAddRow);
        bAddSheet = (Button) findViewById(R.id.bAddSheet);
        bClear = (Button) findViewById(R.id.bClear);

		txtNameSheet = (EditText) findViewById(R.id.txtNameSheet);
        txtFilePath = (TextView) findViewById(R.id.txtFilePath);
        txtFilePath.setText("/mnt/sdcard");
		// настраиваем меню
        popup = new PopupMenu(ctx, bAddSheet);
        Menu menu = popup.getMenu();

        popup.getMenuInflater().inflate(R.menu.com_ui_popup, menu);
        popup.setOnMenuItemClickListener(pMenuOnItemClick);
        popup.setOnDismissListener(pMenuDissmissListener);
        bAddRow.setOnClickListener(clickListener);
        bClear.setOnClickListener(clickListener);
        bAddSheet.setOnClickListener(clickListener);
        bOpen.setOnClickListener(clickListener);
        bSave.setOnClickListener(clickListener);
        bSaveAs.setOnClickListener(clickListener);
		// создаем дискретную палитру (библиотека jxl не предоставляет необходимых возможностей)
		currentHandler = new Grafics(3, 3); 
		jxl.format.Colour[] allColours = (jxl.format.Colour[]) jxl.format.Colour.AUTOMATIC.getAllColours();
		for (int i=0;i < allColours.length;i++)
		{
			String hexVal = String.format("%H", ((jxl.format.Colour)allColours[i]).getValue());
			for (int j = hexVal.length(); j < 6; j ++)
				hexVal = "0" + hexVal;
			hexVal = "#FF" + hexVal;
			discretColorPallite.put(((jxl.format.Colour)allColours[i]).getDescription()
									, currentHandler.createFilledBitmap(Color.parseColor(hexVal)).mutate());


		}
		// корректируем настройку фона по-умолчанию
		discretColorPallite.put("defaut background"
								, currentHandler.createFilledBitmap(Color.parseColor("#FFFFFFFF")).mutate());
    }

    private void setSpinner()
    {
        String[]  arSheets = new String[]{};
        arSheets = allSheets.keySet().toArray(arSheets);
        spnAdapter = new ArrayAdapter<String>(ctx,
                                              android.R.layout.simple_spinner_dropdown_item, arSheets);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSheetSelector.setAdapter(spnAdapter);
        spnSheetSelector.setOnItemSelectedListener(spnOnSelectedItem);
    }
    /**<h2>Описание</h2>
     * <p>Получение максимального размера колонок двумерного хранилища</p>
     * @param arg - двумерная структура, содержащая данные таблицы
     * @return максимальное количество колонок таблицы
     */
	private int getMaxColumnsSize(HashMap<String,ArrayList<TextView>> arg)
    {
        return arg.size();
    }
    /**<h2>Описание</h2>
     * <p>Получение максимального размера рядов двумерного хранилища</p>
     * @param arg - двумерная структура, содержащая данные таблицы
     * @return  максимальное количество рядов таблицы
     */
    private int getMaxRowsSize(HashMap<String,ArrayList<TextView>> arg)
    {
        // перебираем каждую колонку и проверяем данные каждого
        // ряда и выводим максимальный
        int max = 0;
        Iterator iter = arg.keySet().iterator();
        while (iter.hasNext())
        {
            String column = iter.next().toString();
            int s = arg.get(column).size();
            if (s > max)
            {
                max = s;
            }
        }
        return max;
    }
    /**<h2>Описание</h2>
     * <p>Преобразование номера столбца в формат исчисления заданного
     * строкой, являющейся последовательным набором символов 
     * - базой исчисления</p>
     * @param index - число для преобразования
     * @param alfavitAZ - строка база исчисления
     * @return строка для использования в качестве адреса
     */
    private static String enumAlfa(int dec, final String alfavitAZ)
    {
        String res = "";
        // размер базы системы счисления
        int base = alfavitAZ.length();
        // разряд алфавитного идентификатора, являющийся степенью длины строки базы
        // 
        int n = 0, razryad = 0, mod = 0 ;
        // пока разрядность не превысила  значения в исходной системе счисления
        do
        {   
            razryad = dec / (base);
            if (razryad > 0)
            {
                res = res + alfavitAZ.substring(razryad - 1, razryad);
            }
            if (n == 0)
            {
                mod = dec % (base);
//            if( mod > 0)
//            {
                res = res + alfavitAZ.substring(mod, mod + 1);
//            }
            }

            // вычисляем остаток


            System.out.printf("A razryad is: %d\n", razryad);
            System.out.printf("A mod is: %d\n", mod);
            System.out.printf("A poryadok is: %d\n", n);
            n++;
        }while(Math.pow(base, n + 1) < dec);
        return res;
    }
	private PopupMenu.OnDismissListener pMenuDissmissListener;
    private PopupMenu.OnMenuItemClickListener pMenuOnItemClick = new PopupMenu.OnMenuItemClickListener() {
        public boolean onMenuItemClick(MenuItem item)
        {
            if (item != mnuDelete)
			{
                txtFilePath.setVisibility(View.GONE);
                txtNameSheet.setVisibility(View.VISIBLE);
            }
            bAddSheet.setText(ctx.getString(R.string.filemanager_OK));
            if (item == mnuDelete)
			{
                operationMode = OperationMode.DELETE;

            }
            if (item == mnuAdd)
			{
                operationMode = OperationMode.CREATE_SHEET;
            }
            if (item == mnuRename)
			{
                operationMode = OperationMode.RENAME_SHEET;
            }
            return true;
        }
    };
    private View.OnClickListener onClickButton = new View.OnClickListener(){
        @Override
        public void onClick(View v)
        {
            File operating = null;
            switch (operationMode)
			{
                case OperationMode.CREATE_SHEET:
                case OperationMode.RENAME_SHEET:

                    break;
                case OperationMode.DELETE:

                    break;
                case OperationMode.NOTHING:
                default:
                    popup.show();
            }
            switch (operationMode)
			{

                case OperationMode.CREATE_SHEET:

                    break;
                case OperationMode.RENAME_SHEET:

                    break;
                case OperationMode.DELETE:

                    break;
                case OperationMode.NOTHING:
                default:
            }
            switch (operationMode)
			{
                case OperationMode.CREATE_SHEET:
                case OperationMode.RENAME_SHEET:
                case OperationMode.DELETE:
                    operationMode = OperationMode.NOTHING;

                    break;
            }
        }
	};
}
