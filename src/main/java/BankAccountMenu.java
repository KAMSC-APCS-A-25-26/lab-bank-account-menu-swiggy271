import java.util.Scanner;

public class BankAccountMenu {
    public static void main(String[] args) {

        // program setup: initialize variables and scanner
        Scanner scan = new Scanner(System.in);
        int menuChoice;
        double balance = 0, change;
        boolean running = true;

        // continues the program until the user asks not to
        while (running)
        {
            // displays options and reads user choice
            System.out.print("\n\nBank Account Menu\n1. Deposit\n2. Withdraw\n3. Print Balance\n4. Exit\nEnter your choice:  ");
            menuChoice = scan.nextInt();

            switch(menuChoice)
            {
                case 1:
                    // deposit
                    System.out.println("Enter amount to add:  ");
                    change = scan.nextDouble();
                    balance += change;
                    break;
                case 2:
                    // withdrawal
                    System.out.println("Enter amount to withdraw:  ");
                    change = scan.nextDouble();
                    if (change > balance)
                    {
                        System.out.println("Err: Invalid Funds");
                    }
                    else
                    {
                        balance -= change;
                    }
                    break;
                case 3:
                    // check balance
                    System.out.println("Your balance:  " + balance);
                    break;
                case 4:
                    // end program
                    System.out.println("Goodbye!");
                    running = false;
                    break;
            }

        }

    }
}