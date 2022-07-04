import ea.*;
import java.util.Random;

public class GAME extends Game implements Ticker {
    // Graphic Elements
    private final Text start_text;
    private final Farbe yellow = new Farbe(245, 255, 0);
    private final Farbe white = new Farbe(255, 255, 255);

    // Sound
    private final Sound eat_sound = new Sound("src/eat.wav");

    // Snake
    private final SNAKE head;
    private final SNAKE[] elements;
    private int x_pos, y_pos, counter;

    // Fruit
    private FRUIT fruit;
    private int fruit_y;
    private int fruit_x;
    private final Random rand;

    // Std
    private int direction;
    private boolean run;

    public GAME() {
        super(500, 500, "SNAKE", false, true);

        Farbe red = new Farbe(255, 0, 0);

        this.rand = new Random();

        this.start_text = new Text("Press S to start!", 50, 250, 20);
        this.start_text.farbeSetzen(this.white);
        this.show(this.start_text);

        this.fruit_x = 20 + rand.nextInt(24)*20;
        this.fruit_y = 20 + rand.nextInt(24)*20;

        this.fruit = new FRUIT(this.fruit_x, this.fruit_y);
        this.fruit.farbeSetzen(this.yellow);

        this.x_pos = 20;
        this.y_pos = 200;

        this.head = new SNAKE(this.x_pos, this.y_pos);
        this.head.farbeSetzen(red);

        this.run = false;
        this.direction = 0;
        this.counter = 0;
        int max = 100;

        this.elements = new SNAKE[max];

        for (int i = 0; i < max; i++) {
            this.elements[i] = null;
        }

        this.elements[this.counter] = this.head;
        counter+=2;

        this.eat();

        this.manager.anmelden(this, 110);
    }

    public void move(int dir) {
        int x_ = x_pos;
        int y_ = y_pos;

        if (dir == 26) { // Up
            this.y_pos -= 20;
            this.head.bewegen(0, -20);
        }
        if (dir == 27 ) { // Right
            this.x_pos += 20;
            this.head.bewegen(20, 0);
        }
        if (dir == 28) { // Down
            this.y_pos += 20;
            this.head.bewegen(0, 20);
        }
        if (dir == 29) { // Left
            this.x_pos -= 20;
            this.head.bewegen(-20, 0);
        }

        this.move_snake(x_,y_);
    }

    public void move_snake(int xx, int yy)
    {
        int x0 = xx;
        int y0 = yy;
        int x1;
        int y1;

        for (int i = 2; i < counter; i++)
        {
            x1 = elements[i].positionX();
            y1 = elements[i].positionY();

            elements[i].positionSetzen(x0, y0);

            x0 = x1;
            y0 = y1;
        }
    }

    @Override
    public void tasteReagieren(int taste) {
        if (taste == 18) {
            if (!this.run) {
                this.run = true;
                this.delete(this.start_text);
                this.show(this.head);
                this.show(this.fruit);
            }
            else{
                this.beenden();
            }
        }
        if (taste == 26 && this.direction != 28) { // Up
            this.direction = 26;
        }

        if (taste == 27 && this.direction != 29) { // Right
            this.direction = 27;
        }

        if (taste == 28 && this.direction != 26) { // Down
            this.direction = 28;
        }

        if (taste == 29 && this.direction != 27) { // Left
            this.direction = 29;
        }
    }

    @Override
    public void tick() {
        if (this.run) {
            this.move(this.direction);
            this.evaluate(this.x_pos, this.y_pos);
        }
    }

    public void evaluate(int x, int y) {
        this.check_wall_collision(x, y);
        this.check_snake_collision();
        this.check_apple();
    }

    public void check_snake_collision()
    {
        boolean collision = false;
        for (int i = 2; i < counter; i++)
        {
            if(head.schneidet(elements[i]))
            {
                collision = true;
            }
        }
        if(collision)
        {
            this.run = false;
            this.delete(this.fruit);
            this.delete(this.head);
            for(int i = 2; i<this.counter; i++){
                this.delete(this.elements[i]);
            }
            this.showText(this.counter-2);
        }
    }

    public void check_apple() {
        if(head.schneidet(fruit)){
            this.create_fruit();
            this.show(this.fruit);
            if(counter < 99)
            {
                counter++;
            }
            this.eat_sound.play();
            this.eat();
        }
    }

    public void create_fruit(){
        this.delete(this.fruit);
        this.fruit = null;
        this.fruit_x = 20 + rand.nextInt(24)*20;
        this.fruit_y = 20 + rand.nextInt(24)*20;
        this.fruit = new FRUIT(this.fruit_x, this.fruit_y);
        this.fruit.farbeSetzen(this.yellow);

        boolean wrong_spawn = false;

        for(int i = 2; i<this.counter; i++){
            if(this.fruit.schneidet(elements[i])){
                wrong_spawn = true;
            }
        }
        if(wrong_spawn){
            this.create_fruit();
        }
    }

    public void check_wall_collision(int x, int y) {
        if (x < 0 || x > 500) {
            this.run = false;
            this.delete(this.fruit);
            this.delete(this.head);
            for(int i = 2; i<this.counter; i++){
                this.delete(this.elements[i]);
            }
            this.showText(this.counter-2);
        }
        if (y < 0 || y > 500) {
            this.run = false;
            this.delete(this.fruit);
            this.delete(this.head);
            for(int i = 2; i<this.counter; i++){
                this.delete(this.elements[i]);
            }
            this.showText(this.counter-2);
        }
    }

    public void eat() {
        SNAKE new_element = new SNAKE(-40,-40);
        Farbe randFarbe = new Farbe(this.rand.nextInt(256), this.rand.nextInt(256), this.rand.nextInt(256));
        new_element.farbeSetzen(randFarbe);
        this.show(new_element);
        this.elements[this.counter] = new_element;
    }

    public void showText(int score){
        Text lost_text = new Text("You lost! \n Highscore: " + score, 50, 250, 20);
        lost_text.farbeSetzen(this.white);
        this.show(lost_text);
    }


    // Window Structure
    public void show(Raum object) {
        this.wurzel.add(object);
    }

    public void delete(Raum object) {
        this.wurzel.entfernen(object);
    }
}

