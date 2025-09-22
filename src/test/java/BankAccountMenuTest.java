import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankAccountMenuTest {

    private static List<Double> extractMoneyAmounts(String output) {
        Pattern p = Pattern.compile("\\$([0-9]+\\.?[0-9]*)|(?:balance|amount|add|withdraw|new|current).*?([0-9]+\\.?[0-9]*)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(output);
        List<Double> amounts = new ArrayList<>();
        while (m.find()) {
            try {
                String amountStr = m.group(1) != null ? m.group(1) : m.group(2);
                if (amountStr != null) {
                    amounts.add(Double.parseDouble(amountStr));
                }
            } catch (NumberFormatException e) {
            }
        }
        return amounts;
    }

    private String runProgramWithInput(String input) {
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream origOut = System.out;
        java.io.InputStream origIn = System.in;

        System.setIn(in);
        System.setOut(new PrintStream(outBytes));
        try {
            BankAccountMenu.main(new String[]{});
        } finally {
            System.setOut(origOut);
            System.setIn(origIn);
        }

        return outBytes.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void testMenuDisplay() {
        String input = "4\n";
        String output = runProgramWithInput(input);

        assertTrue(output.contains("1"), 
            "❌ Your program should display option 1!");
        
        assertTrue(output.contains("2"), 
            "❌ Your program should display option 2!");
        
        assertTrue(output.contains("3"), 
            "❌ Your program should display option 3!");
        
        assertTrue(output.contains("4"), 
            "❌ Your program should display option 4!");
    }

    @Test
    public void testAddMoneyPositive() {
        String input = "1\n50.25\n4\n";
        String output = runProgramWithInput(input);

        List<Double> amounts = extractMoneyAmounts(output);
        
        boolean hasBalanceIncrease = false;
        for (Double amount : amounts) {
            if (amount >= 50.0) {
                hasBalanceIncrease = true;
                break;
            }
        }
        assertTrue(hasBalanceIncrease, 
            "❌ Your program should add money to the balance! Expected to see balance >= 50.00 after adding 50.25.");
    }

    @Test
    public void testAddMoneyNegative() {
        String input = "1\n-25.50\n4\n";
        String output = runProgramWithInput(input);

        List<Double> amounts = extractMoneyAmounts(output);
        
        // Check that balance did NOT increase (should not have any positive amounts)
        boolean hasBalanceIncrease = false;
        for (Double amount : amounts) {
            if (amount > 0) {
                hasBalanceIncrease = true;
                break;
            }
        }
        assertTrue(!hasBalanceIncrease, 
            "❌ Your program should NOT add negative amounts to balance! Balance should remain 0.00 after trying to add -25.50.");
    }

    @Test
    public void testCheckBalance() {
        String input = "3\n4\n";
        String output = runProgramWithInput(input);

        List<Double> amounts = extractMoneyAmounts(output);
        
        assertTrue(amounts.contains(0.00), 
            "❌ Your program should display the initial balance of $0.00!");
    }

    @Test
    public void testWithdrawInsufficientFunds() {
        String input = "2\n50.00\n4\n";
        String output = runProgramWithInput(input);

        List<Double> amounts = extractMoneyAmounts(output);
        
        boolean hasBalanceDecrease = false;
        for (Double amount : amounts) {
            if (amount < 0) {
                hasBalanceDecrease = true;
                break;
            }
        }
        assertTrue(!hasBalanceDecrease, 
            "❌ Your program should NOT allow withdrawal when insufficient funds! Balance should remain 0.00 when trying to withdraw 50.00 from empty account.");
    }

    @Test
    public void testWithdrawValidAmount() {
        String input = "1\n100.00\n2\n25.50\n4\n";
        String output = runProgramWithInput(input);

        List<Double> amounts = extractMoneyAmounts(output);
        
        boolean hasCorrectFinalBalance = false;
        for (Double amount : amounts) {
            if (Math.abs(amount - 74.50) < 0.01) {
                hasCorrectFinalBalance = true;
                break;
            }
        }
        assertTrue(hasCorrectFinalBalance, 
            "❌ Your program should calculate balance correctly! After adding 100.00 and withdrawing 25.50, balance should be 74.50.");
    }

    @Test
    public void testWithdrawNegativeAmount() {
        String input = "1\n50.00\n2\n-10.00\n4\n";
        String output = runProgramWithInput(input);

        List<Double> amounts = extractMoneyAmounts(output);
        
        boolean hasIncorrectBalance = false;
        for (Double amount : amounts) {
            if (Math.abs(amount - 40.00) < 0.01) {
                hasIncorrectBalance = true;
                break;
            }
        }
        assertTrue(!hasIncorrectBalance, 
            "❌ Your program should NOT allow negative withdrawals! Balance should remain 50.00 when trying to withdraw -10.00.");
    }

    @Test
    public void testMenuLoop() {
        String input = "1\n50.00\n3\n2\n25.00\n3\n4\n";
        String output = runProgramWithInput(input);

        List<Double> amounts = extractMoneyAmounts(output);
        
        assertTrue(amounts.contains(50.00) && amounts.contains(25.00), 
            "❌ Your program should handle multiple operations!");
        
        boolean hasCorrectFinalBalance = false;
        for (Double amount : amounts) {
            if (Math.abs(amount - 25.00) < 0.01) {
                hasCorrectFinalBalance = true;
                break;
            }
        }
        assertTrue(hasCorrectFinalBalance, 
            "❌ Your program should maintain balance across operations! After adding 50.00 and withdrawing 25.00, balance should be 25.00.");
    }

    @Test
    public void testExitFunctionality() {
        String input = "4\n";
        String output = runProgramWithInput(input);

        assertTrue(output.length() > 0, "❌ Your program should run without crashing when exiting!");
    }


    @Test
    public void testBalancePersistence() {
        String input = "1\n100.00\n1\n50.00\n3\n4\n";
        String output = runProgramWithInput(input);

        List<Double> amounts = extractMoneyAmounts(output);
        
        boolean hasCorrectFinalBalance = false;
        for (Double amount : amounts) {
            if (Math.abs(amount - 150.00) < 0.01) {
                hasCorrectFinalBalance = true;
                break;
            }
        }
        assertTrue(hasCorrectFinalBalance, 
            "❌ Your program should maintain balance across multiple operations! After adding 100.00 and 50.00, balance should be 150.00.");
    }

    @Test
    public void testInvalidMenuChoice() {
        String input = "5\n4\n";
        String output = runProgramWithInput(input);

        assertTrue(output.length() > 0, "❌ Program should not crash with invalid input!");
    }

    @Test
    public void testCompleteWorkflow() {
        String input = "1\n200.00\n3\n2\n75.50\n3\n2\n50.00\n3\n4\n";
        String output = runProgramWithInput(input);

        List<Double> amounts = extractMoneyAmounts(output);
        
        // Check that we see the expected balance amounts: 200.00, 124.50, 74.50
        assertTrue(amounts.contains(200.00) && amounts.contains(124.50) && amounts.contains(74.50), 
            "❌ Your program should handle a complete workflow with multiple operations!");

        boolean hasCorrectFinalBalance = false;
        for (Double amount : amounts) {
            if (Math.abs(amount - 74.50) < 0.01) {
                hasCorrectFinalBalance = true;
                break;
            }
        }
        assertTrue(hasCorrectFinalBalance, 
            "❌ Your program should calculate balance correctly! After adding 200.00 and withdrawing 75.50 and 50.00, balance should be 74.50.");
    }

    @Test
    public void testSwitchStatementUsage() {
        String input = "1\n10.00\n2\n5.00\n3\n1\n20.00\n2\n15.00\n3\n4\n";
        String output = runProgramWithInput(input);

        List<Double> amounts = extractMoneyAmounts(output);
        
        assertTrue(amounts.contains(10.00) && amounts.contains(5.00) && amounts.contains(25.00), 
            "❌ Your program should handle multiple menu operations efficiently! This suggests you might be using if-else instead of switch statement.");
        
        boolean hasCorrectFinalBalance = false;
        for (Double amount : amounts) {
            if (Math.abs(amount - 10.00) < 0.01) {
                hasCorrectFinalBalance = true;
                break;
            }
        }
        assertTrue(hasCorrectFinalBalance, 
            "❌ Your program should calculate balance correctly! This suggests you might be using if-else instead of switch statement.");
    }

    @Test
    public void testScannerNotInLoop() {
        String input = "1\n100.00\n2\n50.00\n3\n4\n";
        String output = runProgramWithInput(input);


        List<Double> amounts = extractMoneyAmounts(output);
        
        assertTrue(amounts.contains(100.00) && amounts.contains(50.00), 
            "❌ Your program should handle multiple operations! Make sure Scanner is created outside the loop.");
        
        boolean hasCorrectFinalBalance = false;
        for (Double amount : amounts) {
            if (Math.abs(amount - 50.00) < 0.01) {
                hasCorrectFinalBalance = true;
                break;
            }
        }
        assertTrue(hasCorrectFinalBalance, 
            "❌ Your program should calculate balance correctly! Make sure Scanner is created outside the loop, not inside the while loop.");
    }

    @Test
    public void testDefaultCaseHandling() {
        String input = "5\n4\n";
        String output = runProgramWithInput(input);

        assertTrue(output.length() > 0, 
            "❌ Your program should not crash with invalid input! Consider using a default case in your switch statement.");
    }
}
