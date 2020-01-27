import java.util.regex.Pattern;

public class Cell {
    Integer column, row;
    Integer leftCol = null, leftRow = null, rightCol = null, rightRow = null;
    Integer value = null;
    CellType cellType;
    RefType refType;
    ErrorType errorType;
    Integer left = null, right = null;
    boolean IsReferenceToOneCell = false; // if true, leftCol & leftRow
    Cell leftCell, rightCell;
    private Cell(){

    }
    public Cell(String s, int column, int row) throws Exception {
        this.column = column;
        this.row = row;
        if (s.length() == 0){
            cellType = CellType.Empty;
        }
        else if (s.charAt(0) == '='){
            cellType = CellType.ToBeEvalueated;
            String temp = s.substring(1);
            if ( Contains(temp,'+') ){
                String[] tempParts = temp.split("\\+");
                try {
                    if (IsCellRef(tempParts[0])) {
                        tempParts[0] = tempParts[0].substring(1);
                        leftCol = GetColumnNumber(tempParts[0]);
                        leftRow = GetRowNumber(tempParts[0]);
                    } else {
                        left = Integer.parseInt(tempParts[0]);
                    }

                    if (IsCellRef(tempParts[1])) {
                        tempParts[1] = tempParts[1].substring(1);
                        rightCol = GetColumnNumber(tempParts[1]);
                        rightRow = GetRowNumber(tempParts[1]);
                    } else {
                        right = Integer.parseInt(tempParts[1]);
                    }

                    refType = RefType.Sum;

                    if (left != null && right != null){
                        this.value = left+right;
                        this.cellType = CellType.Value;
                    }
                }
                catch (NumberFormatException e){
                    this.cellType = CellType.Error;
                    this.errorType = ErrorType.CHYBA;
                }
            }
            else if (Contains(temp,'-')){
                String[] tempParts = temp.split("-");
                try {
                    if (IsCellRef(tempParts[0])) {
                        tempParts[0] = tempParts[0].substring(1);
                        leftCol = GetColumnNumber(tempParts[0]);
                        leftRow = GetRowNumber(tempParts[0]);
                    } else {
                        left = Integer.parseInt(tempParts[0]);
                    }

                    if (IsCellRef(tempParts[1])) {
                        tempParts[1] = tempParts[1].substring(1);
                        rightCol = GetColumnNumber(tempParts[1]);
                        rightRow = GetRowNumber(tempParts[1]);
                    } else {
                        right = Integer.parseInt(tempParts[1]);
                    }

                    refType = RefType.Diff;

                    if (left != null && right != null){
                        this.value = left+right;
                        this.cellType = CellType.Value;
                    }
                }
                catch (NumberFormatException e){
                    this.cellType = CellType.Error;
                    this.errorType = ErrorType.CHYBA;
                }
            }
            else if (Contains(temp,'*')) {
                String[] tempParts = temp.split("\\*");
                try {
                    if (IsCellRef(tempParts[0])) {
                        tempParts[0] = tempParts[0].substring(1);
                        leftCol = GetColumnNumber(tempParts[0]);
                        leftRow = GetRowNumber(tempParts[0]);
                    } else {
                        left = Integer.parseInt(tempParts[0]);
                    }

                    if (IsCellRef(tempParts[1])) {
                        tempParts[1] = tempParts[1].substring(1);
                        rightCol = GetColumnNumber(tempParts[1]);
                        rightRow = GetRowNumber(tempParts[1]);
                    } else {
                        right = Integer.parseInt(tempParts[1]);
                    }

                    refType = RefType.Mul;

                    if (left != null && right != null){
                        this.value = left+right;
                        this.cellType = CellType.Value;
                    }
                }
                catch (NumberFormatException e){
                    this.cellType = CellType.Error;
                    this.errorType = ErrorType.CHYBA;
                }
            }
            else if (Contains(temp,'/')) {
                String[] tempParts = temp.split("/");
                try {
                    if (IsCellRef(tempParts[0])) {
                        tempParts[0] = tempParts[0].substring(1);
                        leftCol = GetColumnNumber(tempParts[0]);
                        leftRow = GetRowNumber(tempParts[0]);
                    } else {
                        left = Integer.parseInt(tempParts[0]);
                    }

                    if (IsCellRef(tempParts[1])) {
                        tempParts[1] = tempParts[1].substring(1);
                        rightCol = GetColumnNumber(tempParts[1]);
                        rightRow = GetRowNumber(tempParts[1]);
                    } else {
                        right = Integer.parseInt(tempParts[1]);
                    }

                    refType = RefType.Div;

                    if (left != null && right != null){
                        this.value = left+right;
                        this.cellType = CellType.Value;
                    }
                }
                catch (NumberFormatException e){
                    this.cellType = CellType.Error;
                    this.errorType = ErrorType.CHYBA;
                }
            }
            else{
                if ( IsCellRef(temp) ){
                    IsReferenceToOneCell = true;
                    temp = temp.substring(1);
                    leftCol = GetColumnNumber(temp);
                    leftRow = GetRowNumber(temp);
                }
                else{
                    cellType = CellType.Error;
                    errorType = ErrorType.CHYBA;
                }
            }
        }
        else { //is value
            this.value = Integer.parseInt(s);
            cellType = CellType.Value;
        }
    }

    private int GetColumnNumber(String s){
        return s.charAt(0) - 'A';
    }
    private int GetRowNumber(String s){
        String temp = s.substring(1);
        return Integer.parseInt(temp) - 1;
    }
    private boolean Contains(String s, char ch){
        boolean res = false;
        for (int i = 0; i<s.length(); i++){
            if (s.charAt(i) == ch){
                res = true;
                break;
            }
        }
        return res;
    }
    private static boolean IsCellRef( String s ){
        Pattern p = Pattern.compile("\\$[A-Z][1-9][0-9]*");
        return p.matcher(s).matches();
    }
    public static Cell EmptyCell(int col, int row) throws Exception {
        return new Cell("",col,row);
    }
    public static Cell ErrorCell(ErrorType type, int column, int row){
        Cell cell = new Cell();
        cell.cellType = CellType.Error;
        cell.errorType = type;
        return cell;
    }
    @Override
    public String toString(){
        if (cellType == CellType.Error){
            return "#" + errorType.toString();
        }
        else if (cellType == CellType.Empty){
            return "";
        }
        else if (cellType == CellType.Value){
            return value.toString();
        }
        else return "In Progress";
    }

}
