class DataDisplay {
    String name;
    String[][] chart = new String[24][80];
    long[] value;
    String[] range;

    // For Tabular Display
    public DataDisplay(String n, long[] v) {
        name = n;
        value = v;
    }

    // For Chart Display
    public DataDisplay(String n, long[] v, String[] r) {
        name = n;
        value = v;
        range = r;
    }

    // For Chart Display
    public void createChart() {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 80; j++) {
                if (i == 23) {
                    chart[i][j] = "_";
                } else if (j == 0) {
                    chart[i][j] = "|";
                } else {
                    chart[i][j] = " ";
                }
                if (i == 23 && j == 0) {
                    chart[i][j] = "|";
                }
            }
        }
    }

    // For Chart Display
    public void displayChart() {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 80; j++) {
                System.out.print(chart[i][j]);
            }
            System.out.println();
        }
    }

    // For Chart Display
    public void representData() {
        double max = getMax();
        double min = getMin();
        double valueRange = 22/(max - min + 1); //deltaY/deltaV
        int distanceBetweenColumn = 78/(value.length);
        int y; // y -- i
        int x = 1; // x -- j


        for (int index = 0; index < value.length; index++) {
            if (value[index] == min) {
                chart[22][x] = "*";
            } else {
                double valuePoint = (value[index] * valueRange) - min*valueRange;
                if (valuePoint < 1) {
                    valuePoint = 1;
                }
                y = (int) Math.ceil(23 - valuePoint);
                chart[y][x] = "*";
            }

            x += distanceBetweenColumn;

        }
        System.out.println("---------------------------------- CHART TABLE ----------------------------------");
    }

    // For Tabular Display
    public void createTable() {
        System.out.println("|           Range           | Value |");
        System.out.println("-------------------------------------");
        for (int i = 0; i < range.length; i++) {
            System.out.printf("|\t" +range[i] +"\t\t|");
            System.out.printf("\t" +value[i] + "\t|\n");
        }
    }

    public long getMax() {
        Long max = value[0];
        for (int i = 1; i < value.length; i++) {
            if (value[i] > max) {
                max = value[i];
            }
        }
        return max;
    }

    public long getMin() {
        long min = value[0];
        for (int i = 1; i < value.length; i++) {
            if (value[i] < min) {
                min = value[i];
            }
        }
        return min;
    }
}

