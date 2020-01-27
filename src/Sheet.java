import jdk.jshell.spi.ExecutionControl;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

public class Sheet {
    public String fileName;
    public ArrayList<ArrayList<Cell>> cells;
    private Stack<Cell> stack;
    private Sheet(String fileName){
    }
    public static Sheet FromCSVFile(String fileName) throws Exception {
        Sheet sheet = new Sheet(fileName);
        sheet.cells = new ArrayList<ArrayList<Cell>>();


        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))){
            String line;
            int rowCounter = 0;
            int colCounter = 0;
            while ( (line = br.readLine()) != null ){
                String[] lineParts = line.split(";");
                ArrayList<Cell> temp = new ArrayList<Cell>();
                colCounter = 0;
                for (int i = 0; i < lineParts.length; i++){
                    Cell tempCell = new Cell(lineParts[i], colCounter, rowCounter);
                    temp.add(tempCell);
                    colCounter++;
                }

                sheet.cells.add(temp);
                rowCounter++;
            }
            return sheet;
        }
        catch (IOException e ){
            System.out.println("IOException occured");
            return null;
        }
    }

    public void evalueate() throws Exception {
        for (int i = 0; i < cells.size(); i++){
            for (int k = 0; k < cells.get(i).size(); k++){
                if (cells.get(i).get(k).cellType == CellType.ToBeEvalueated){
                    DFS(i,k);
                }
            }
        }
    }
    private void DFS(int i, int k) throws Exception { // is started only on reference cells
        stack = new Stack<Cell>();
        stack.push(cells.get(i).get(k));
        DFSStep();
        stack.pop();
        stack = null;
    }

    private void DFSStep() throws Exception {
        Cell tempCell = stack.peek();
        int leftValue, rightValue;
        if (tempCell.IsReferenceToOneCell){
            int col = tempCell.leftCol;
            int row = tempCell.leftRow;
            if (row >= cells.size() || col >= cells.get(row).size()){
                cells.get(tempCell.row).set(tempCell.column, Cell.EmptyCell(tempCell.column, tempCell.row));
            }
            else {
                Cell refCell = cells.get(row).get(col);
                if (refCell.cellType == CellType.Value) {
                    Cell newCell = new Cell( refCell.toString(),tempCell.column, tempCell.row);
                    cells.get(tempCell.row).set(tempCell.column, refCell);
                }
                else if ((refCell.cellType == CellType.Empty)){
                    cells.get(tempCell.row).set(tempCell.column, Cell.EmptyCell(tempCell.column, tempCell.row));
                }
                else if ((refCell.cellType == CellType.Error)){
                    cells.get(tempCell.row).set(tempCell.column, Cell.ErrorCell( ErrorType.CHYBA, tempCell.column, tempCell.row));
                }
                else if ((refCell.cellType == CellType.ToBeEvalueated)){
                    if (stack.contains(refCell)){
                        Cell[] cellArr = new Cell[stack.size()];
                        stack.toArray(cellArr);
                        //check stack
                        for (int i = 0; i < cellArr.length; i++){
                            if (cellArr[i] == refCell){
                                for (int k = i; k < cellArr.length; k++){
                                    Cell curCell = cellArr[k];
                                    cells.get(curCell.row).set(curCell.column, Cell.ErrorCell( ErrorType.CYKLUS, curCell.column, curCell.row));
                                }
                            }
                            break;
                        }
                    }
                    else {
                        stack.push(refCell);
                        DFSStep();
                        stack.pop();
                        refCell = cells.get(row).get(col);
                        if (refCell.cellType == CellType.Value) {
                            int value = cells.get(row).get(col).value;
                            cells.get(tempCell.row).set(tempCell.column, new Cell(value + "", tempCell.column, tempCell.row));
                        }
                        else if (refCell.cellType == CellType.Error && refCell.errorType == ErrorType.CYKLUS){
//                            cells.get(tempCell.row).set(tempCell.column, Cell.ErrorCell(ref, tempCell.column, tempCell.row));
                        }

                    }

                }
            }
        }
        else {
            if (tempCell.left == null) { // left is reference
                int col = tempCell.leftCol;
                int row = tempCell.leftRow;

                if (row >= cells.size() || col >= cells.get(row).size()) {
                    tempCell.left = 0;
                } else {
                    Cell refCell = cells.get(row).get(col);
                    if (refCell.cellType == CellType.Value){
                        tempCell.left = refCell.value;
                    }
                    else if (refCell.cellType == CellType.Error){
                        cells.get(tempCell.row).set(tempCell.column, Cell.ErrorCell( ErrorType.CHYBA, tempCell.column, tempCell.row));
                    }
                    else if (refCell.cellType == CellType.ToBeEvalueated) {
                        stack.push(cells.get(row).get(col));
                        DFSStep();
                        stack.pop();
                    }
                }
            }
            leftValue = tempCell.left;

            if (tempCell.right == null) { // right is reference
                int col = tempCell.rightCol;
                int row = tempCell.rightRow;

                if (row >= cells.size() || col >= cells.get(row).size()) {
                    tempCell.right = 0;
                } else {
                    Cell refCell = cells.get(row).get(col);
                    if (refCell.cellType == CellType.Value){
                        tempCell.right = refCell.value;
                    }
                    else if (refCell.cellType == CellType.Empty){
                        tempCell.right = 0;
                    }
                    else if (refCell.cellType == CellType.Error){
                        cells.get(tempCell.row).set(tempCell.column, Cell.ErrorCell( ErrorType.CHYBA, tempCell.column, tempCell.row));
                    }
                    else if (refCell.cellType == CellType.ToBeEvalueated) {
                        stack.push(cells.get(row).get(col));
                        DFSStep();
                        stack.pop();
                    }
                }
            }
            rightValue = tempCell.right;
            if (tempCell.refType == RefType.Sum){
                cells.get(tempCell.row).set(tempCell.column, new Cell((leftValue + rightValue) + "", tempCell.column, tempCell.row));
            }
            else if (tempCell.refType == RefType.Diff){
                cells.get(tempCell.row).set(tempCell.column, new Cell((leftValue - rightValue) + "", tempCell.column, tempCell.row));
            }
            else if (tempCell.refType == RefType.Mul){
                cells.get(tempCell.row).set(tempCell.column, new Cell((leftValue * rightValue) + "", tempCell.column, tempCell.row));
            }
            else if (tempCell.refType == RefType.Div){
                if (rightValue == 0){
                    cells.get(tempCell.row).set(tempCell.column, Cell.ErrorCell(ErrorType.NAN, tempCell.column, tempCell.row));
                }
                else {
                    cells.get(tempCell.row).set(tempCell.column, new Cell((leftValue / rightValue) + "", tempCell.column, tempCell.row));
                }
            }

        }
        //else if ()
    }

    public void printOut(BufferedWriter bufferedWriter) throws NoSuchMethodException, IOException {
        for (int row = 0; row < cells.size(); row++){
            for (int col = 0; col < cells.get(row).size(); col++){
                if (col > 0){
                    bufferedWriter.write(";");
                }
                bufferedWriter.write(cells.get(row).get(col).toString());
            }
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
    }
}
