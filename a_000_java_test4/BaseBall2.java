import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.*;

public class BaseBall {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));

    public static int playGame() throws IOException {
        return playGame(generateRound(), 0);
    }

    public static int playGame(Round computerRound, int playCount) throws IOException {
        System.out.println("===== [" + (playCount + 1) + "회] =====");
        // System.out.println(computerRound.toString());

        Round userRound = processInput();
        CompareResult result = computerRound.compare(userRound);

        if (result.strike == 3) {
            return playCount + 1;
        }

        System.out.println((playCount + 1) + "회 결과 - 스트라이크: " + result.strike + ", 볼: " + result.ball);
        return playGame(computerRound, playCount + 1);
    }

    public static Round processInput() throws IOException {
        System.out.print("숫자 3개를 입력하세요: ");
        String[] data = READER.readLine().split(" ");

        if (data.length != 3) {
            System.out.println("숫자를 3개만 입력해야합니다! 다시 입력해주세요.");
            return processInput();
        } else if (!isInteger(data)) {
            System.out.println("숫자를 입력해야합니다! 다시 입력해주세요.");
            return processInput();
        }

        int[] array = Arrays.stream(data).mapToInt(Integer::parseInt).toArray();
        Round round = new Round(array);
        InvalidType invalidType = round.findProblem();

        if (invalidType != null) {
            System.out.println(invalidType.getErrorMessage() + "! 다시 입력해주세요.");
            return processInput();
        }

        return round;
    }

    public static void main(String[] args) throws IOException {
        int result = playGame();

        if (result <= 2)
            System.out.println("참잘했어요");
        else if (result <= 5)
            System.out.println("잘했어요");
        else if (result <= 9)
            System.out.println("보통이네요");
        else
            System.out.println("분발하세요!");
    }

    public static Round generateRound() {
        int x, y, z;

        do {
            x = RANDOM.nextInt(1, 9);
            y = RANDOM.nextInt(1, 9);
            z = RANDOM.nextInt(1, 9);
        } while (x == y || x == z || y == z);

        return new Round(x, y, z);
    }

    public static boolean isInteger(String... texts) {
        try {
            for (String text : texts)
                Integer.parseInt(text);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public static class Round {
        public int[] data;

        public Round(int x, int y, int z) {
            this(new int[]{x, y, z});
        }

        public Round(int[] data) {
            this.data = data;
        }
        
        public InvalidType findProblem() {
            return Arrays.stream(InvalidType.values()).filter(type -> type.test(this)).findFirst().orElse(null);
        }

        public CompareResult compare(Round other) {
            int strike = 0, ball = 0;
            
            for (int i = 0; i < 3; i++) {
                if (data[i] == other.data[i]) ++strike;
                else if (data[i] == other.data[(i + 1) % 3]) ++ball;
                else if (data[i] == other.data[(i + 2) % 3]) ++ball;
            }
            
            return new CompareResult(strike, ball);
        }
        
        public IntStream toStream() {
            return Arrays.stream(data);
        }

        @Override
        public String toString() {
            return "Round{" +
                    "data=" + Arrays.toString(data) +
                    '}';
        }
    }

    public enum InvalidType {
        SAME_NUMBER(round -> round.data[0] == round.data[1] || round.data[1] == round.data[2] || round.data[0] == round.data[2], "모두 다른 숫자를 입력해주세요"),
        OUT_OF_RANGE(round -> round.toStream().anyMatch(number -> number < 1 || number > 9), "1부터 9까지의 숫자 중 하나를 입력해주세요");
        
        final Predicate<Round> predicate;
        final String message;
        
        InvalidType(Predicate<Round> predicate, String message) {
            this.predicate = predicate;
            this.message = message;
        }
        
        public boolean test(Round round) {
            return predicate.test(round);
        }
        
        public String getErrorMessage() {
            return message;
        }
    }

    public static class CompareResult {
        public final int strike, ball;

        public CompareResult(int strike, int ball) {
            this.strike = strike;
            this.ball = ball;
        }
    }

    //public static record CompareResult(int strike, int ball) {
    //}

}
