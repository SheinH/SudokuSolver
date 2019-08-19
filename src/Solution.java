class Solution {


    static class PossibleSet {

        final boolean[] arr;
        int count;

        PossibleSet() {
            count = 9;
            arr = new boolean[9];
            for (int i = 0; i < 9; i++) {
                arr[i] = true;
            }
        }

        void add(char c) {
            if (!Character.isDigit(c))
                return;
            if (arr[c - '1'])
                return;
            arr[c - '1'] = true;
            count++;
        }

        void remove(char c) {
            if (!Character.isDigit(c))
                return;
            if (!arr[c - '1'])
                return;
            arr[c - '1'] = false;
            count--;
        }

        char[] toArray() {
            char[] rarr = new char[count];
            int i = 0;
            for (int j = 0; j < arr.length; j++) {
                if (arr[j]) {
                    rarr[i] = (char) ('1' + j);
                    i++;
                }
            }
            return rarr;
        }
    }

    static class Point {
        final int y,x;

        public Point(int y, int x) {
            this.y = y;
            this.x = x;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (y != point.y) return false;
            return x == point.x;

        }

        @Override
        public int hashCode() {
            int result = y;
            result = 31 * result + x;
            return result;
        }
    }

    // Utility Methods
    static Point get_square(Point p) {
        int y = (p.y / 3) * 3;
        int x = (p.x / 3) * 3;
        return new Point(y, x);
    }

    static String grid_to_string(char[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (char[] ar : grid)
            for (char c : ar)
                sb.append(c);
        return sb.toString();
    }

    static char[][] string_to_grid(String str) {
        char[][] g = new char[9][9];
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            int y = i / 9;
            int x = i % 9;
            g[y][x] = c;
        }
        return g;
    }

    static PossibleSet get_possible(char[][] grid, int y, int x) {
        PossibleSet ps = new PossibleSet();
        Point[] neighbors = neighbor_set(new Point(y, x));
        for (Point p : neighbors) {
            ps.remove(grid[p.y][p.x]);
        }
        return ps;
    }
    static Point[] neighbor_set(Point p){
        Point[] arr = new Point[20];
        int i = 0;
        for (int y = 0; y < 9; y++) {
            if(y != p.y)
                arr[i++] = new Point(y, p.x);
        }
        for (int x = 0; x < 9; x++) {
            if(x != p.x)
                arr[i++] = new Point(p.y, x);
        }
        Point p2 = get_square(p);
        for (int dy = 0; dy < 3; dy++) {
            for (int dx = 0; dx < 3; dx++) {
                int y = p2.y + dy;
                int x = p2.x + dx;
                if(y != p.y && x != p.x)
                    arr[i++] = new Point(y,x);
            }
        }
        return arr;
    }


    static boolean change_point(char[][] grid, PossibleSet[][] possibleSetGrid, Point p, char newVal) {
        char old = grid[p.y][p.x];
        boolean returnval = true;
        if (newVal == '.') {
            grid[p.y][p.x] = newVal;
            Point[] neighbors = neighbor_set(p);
            for (Point neighbor : neighbors) {
                if (possibleSetGrid[neighbor.y][neighbor.x] == null)
                    continue;
                Point[] neighbors2 = neighbor_set(neighbor);
                boolean found_match = false;
                for (Point n2 : neighbors2) {
                    if (grid[n2.y][n2.x] == old) {
                        found_match = true;
                        break;
                    }
                }
                if (!found_match)
                    possibleSetGrid[neighbor.y][neighbor.x].add(old);
            }
            possibleSetGrid[p.y][p.x] = get_possible(grid, p.y, p.x);
            return true;
        } else {
            Point[] neighbors = neighbor_set(p);
            for (Point n : neighbors) {
                if (possibleSetGrid[n.y][n.x] != null) {
                    possibleSetGrid[n.y][n.x].remove(newVal);
                    if (possibleSetGrid[n.y][n.x].count == 0)
                        returnval = false;
                }
            }
            grid[p.y][p.x] = newVal;
            possibleSetGrid[p.y][p.x] = null;
            return returnval;
        }
    }

    static String solve(String grid) {
        char[][] g = string_to_grid(grid);
        PossibleSet[][] ps = new PossibleSet[9][9];
        int count = 0;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (g[y][x] == '.')
                    ps[y][x] = get_possible(g, y, x);
                else
                    count++;
            }
        }
        solve_rec(g, ps, count);
        return grid_to_string(g);
    }

    static boolean solve_rec(char[][] grid, PossibleSet[][] possibleSetGrid, int solvedCount) {
        if (solvedCount == 81)
            return true;
        Point p = null;
        int min_found = Integer.MAX_VALUE;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                PossibleSet pos = possibleSetGrid[y][x];
                if (pos == null)
                    continue;
                if (pos.count == 0)
                    return false;
                if (pos.count == 1) {
                    p = new Point(y, x);
                    min_found = 1;
                    break;
                } else if (possibleSetGrid[y][x].count < min_found) {
                    p = new Point(y, x);
                    min_found = possibleSetGrid[y][x].count;
                }
            }
            if (min_found == 1)
                break;
        }
        char[] choices = possibleSetGrid[p.y][p.x].toArray();
        int i = 0;
        while (i != choices.length) {
            boolean result;
            if (!change_point(grid, possibleSetGrid, p, choices[i])) {
                result = false;
            } else {
                result = solve_rec(grid, possibleSetGrid, solvedCount + 1);
            }
            if (result)
                return true;
            else {
                i++;
                change_point(grid, possibleSetGrid, p, '.');
            }
        }
        return false;
    }

    static void prettyPrint(String str) {

        for (int i = 0; i < 81; i++) {
            System.out.print(str.charAt(i));
            if ((i + 1) % 3 == 0)
                System.out.print(" ");
            if ((i + 1) % 9 == 0)
                System.out.println();
            if ((i + 1) % 27 == 0)
                System.out.println();
        }
        System.out.println();
        System.out.println();
    }


    public static void main(String[] args) {
        String str = "..9748...7.........2.1.9.....7...24..64.1.59..98...3.....8.3.2.........6...2759..";
        String out = Solution.solve(str);
        prettyPrint(out);
    }
}
