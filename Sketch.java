import processing.core.PApplet;
import java.util.ArrayList;

public class Sketch extends PApplet {

  // Define rectangle properties
  float rectX, rectY;  // Position
  float rectWidth = 50, rectHeight = 50;  // Size
  float rectSpeedX = 0, rectSpeedY = 0;  // Speed
  float gravity = 0.5f;  // Gravity force
  float jumpStrength = -10;  // Jump strength
  boolean isGrounded = false;  // Is the rectangle on the ground?

  // Define platforms and obstacles
  ArrayList<Platform> platforms = new ArrayList<>();
  ArrayList<Obstacle> obstacles = new ArrayList<>();
  ArrayList<MovingPlatform> movingPlatforms = new ArrayList<>();
  ArrayList<Enemy> enemies = new ArrayList<>();
  ArrayList<Collectible> collectibles = new ArrayList<>();

  // Define score and win/lose conditions
  int score = 0;
  int targetScore = 5;
  boolean gameWon = false;
  boolean gameOver = false;
  boolean gameStarted = false;
  int currentLevel = 0;

  public void settings() {
    size(800, 600);
  }

  public void setup() {
    background(255, 255, 255);
    rectX = width / 2 - rectWidth / 2;
    rectY = height - rectHeight;
    initializeLevel();
  }

  public void draw() {
    if (!gameStarted) {
      displayStartScreen();
      return;
    }

    if (gameWon) {
      displayWinMessage();
      return;
    }

    if (gameOver) {
      displayGameOverMessage();
      return;
    }

    background(255, 255, 255);

    // Apply gravity
    rectSpeedY += gravity;

    // Update rectangle position
    rectX += rectSpeedX;
    rectY += rectSpeedY;

    // Check for collision with borders
    if (rectX <= 0 || rectX + rectWidth >= width) {
      rectSpeedX = 0;
      if (rectX <= 0) rectX = 0;
      if (rectX + rectWidth >= width) rectX = width - rectWidth;
    }
    if (rectY + rectHeight >= height) {
      rectSpeedY = 0;
      rectY = height - rectHeight;
      isGrounded = true;
    }

    // Check for collision with platforms
    isGrounded = false;
    for (Platform platform : platforms) {
      checkCollisionWithPlatform(platform);
    }

    // Check for collision with moving platforms
    for (MovingPlatform platform : movingPlatforms) {
      checkCollisionWithPlatform(platform);
      updateMovingPlatform(platform);
    }

    // Check for collision with obstacles
    for (Obstacle obstacle : obstacles) {
      if (rectX < obstacle.x + obstacle.width && rectX + rectWidth > obstacle.x &&
          rectY < obstacle.y + obstacle.height && rectY + rectHeight > obstacle.y) {
        gameOver = true;
      }
    }

    // Check for collision with enemies
    for (Enemy enemy : enemies) {
      if (rectX < enemy.x + enemy.width && rectX + rectWidth > enemy.x &&
          rectY < enemy.y + enemy.height && rectY + rectHeight > enemy.y) {
        gameOver = true;
      }
      updateEnemy(enemy);
    }

    // Check for collision with collectibles
    for (int i = collectibles.size() - 1; i >= 0; i--) {
      Collectible collectible = collectibles.get(i);
      if (rectX < collectible.x + collectible.size && rectX + rectWidth > collectible.x &&
          rectY < collectible.y + collectible.size && rectY + rectHeight > collectible.y) {
        score++;
        collectibles.remove(i);
      }
    }

    // Check for win condition
    if (score >= targetScore) {
      gameWon = true;
    }

    // Draw the rectangle
    fill(0, 0, 255);
    rect(rectX, rectY, rectWidth, rectHeight);

    // Draw the platforms
    fill(100);
    for (Platform platform : platforms) {
      rect(platform.x, platform.y, platform.width, platform.height);
    }

    // Draw the moving platforms
    fill(150);
    for (MovingPlatform platform : movingPlatforms) {
      rect(platform.x, platform.y, platform.width, platform.height);
    }

    // Draw the obstacles
    fill(255, 0, 0);
    for (Obstacle obstacle : obstacles) {
      rect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
    }

    // Draw the enemies
    fill(0, 255, 0);
    for (Enemy enemy : enemies) {
      rect(enemy.x, enemy.y, enemy.width, enemy.height);
    }

    // Draw the collectibles
    fill(255, 255, 0);
    for (Collectible collectible : collectibles) {
      ellipse(collectible.x + collectible.size / 2, collectible.y + collectible.size / 2, collectible.size, collectible.size);
    }

    // Display score
    fill(0);
    textSize(16);
    text("Score: " + score, 10, 20);
  }

  private void initializeLevel() {
    platforms.clear();
    movingPlatforms.clear();
    obstacles.clear();
    enemies.clear();
    collectibles.clear();

    if (currentLevel == 0) {
      platforms.add(new Platform(100, 500, 200, 20));
      platforms.add(new Platform(350, 400, 150, 20));
      platforms.add(new Platform(550, 300, 100, 20));

      movingPlatforms.add(new MovingPlatform(200, 200, 100, 20, 2, 0));

      obstacles.add(new Obstacle(250, 550, 50, 50));

      enemies.add(new Enemy(100, 450, 50, 50, 1, 0));

      collectibles.add(new Collectible(400, 200, 20));
      collectibles.add(new Collectible(600, 150, 20));
      collectibles.add(new Collectible(700, 100, 20));
    } else if (currentLevel == 1) {
      // Add different platforms, obstacles, enemies, and collectibles for level 1
      platforms.add(new Platform(50, 450, 200, 20));
      platforms.add(new Platform(300, 350, 150, 20));
      platforms.add(new Platform(500, 250, 100, 20));
      platforms.add(new Platform(700, 150, 100, 20));

      movingPlatforms.add(new MovingPlatform(150, 300, 100, 20, 2, 0));
      movingPlatforms.add(new MovingPlatform(350, 150, 100, 20, 0, 2));

      obstacles.add(new Obstacle(300, 500, 50, 50));
      obstacles.add(new Obstacle(500, 450, 50, 50));

      enemies.add(new Enemy(200, 400, 50, 50, 1, 0));
      enemies.add(new Enemy(400, 350, 50, 50, 1, 0));

      collectibles.add(new Collectible(200, 250, 20));
      collectibles.add(new Collectible(500, 200, 20));
      collectibles.add(new Collectible(700, 100, 20));
    }
    // Add more levels as needed
  }

  private void checkCollisionWithPlatform(Platform platform) {
    // Vertical collision
    if (rectX < platform.x + platform.width && rectX + rectWidth > platform.x) {
      if (rectY + rectHeight > platform.y && rectY < platform.y) {
        rectY = platform.y - rectHeight;
        rectSpeedY = 0;
        isGrounded = true;
      }
    }

    // Horizontal collision
    if (rectY < platform.y + platform.height && rectY + rectHeight > platform.y) {
      if (rectX + rectWidth > platform.x && rectX < platform.x + platform.width) {
        if (rectSpeedX > 0) {
          rectX = platform.x - rectWidth;
        } else if (rectSpeedX < 0) {
          rectX = platform.x + platform.width;
        }
        rectSpeedX = 0;
      }
    }
  }

  private void updateMovingPlatform(MovingPlatform platform) {
    platform.x += platform.speedX;
    platform.y += platform.speedY;

    // Reverse direction if platform hits the border
    if (platform.x <= 0 || platform.x + platform.width >= width) {
      platform.speedX *= -1;
    }
    if (platform.y <= 0 || platform.y + platform.height >= height) {
      platform.speedY *= -1;
    }
  }

  private void updateEnemy(Enemy enemy) {
    enemy.x += enemy.speedX;
    enemy.y += enemy.speedY;

    // Reverse direction if enemy hits the border
    if (enemy.x <= 0 || enemy.x + enemy.width >= width) {
      enemy.speedX *= -1;
    }
    if (enemy.y <= 0 || enemy.y + enemy.height >= height) {
      enemy.speedY *= -1;
    }
  }

  private void displayStartScreen() {
    background(0, 0, 255);
    fill(255);
    textSize(32);
    textAlign(CENTER, CENTER);
    text("Press ENTER to Start", width / 2, height / 2);
  }

  private void displayWinMessage() {
    background(0, 255, 0);
    fill(0);
    textSize(32);
    textAlign(CENTER, CENTER);
    text("You Win!", width / 2, height / 2);
    textSize(16);
    text("Press ENTER to Next Level", width / 2, height / 2 + 50);
  }

  private void displayGameOverMessage() {
    background(255, 0, 0);
    fill(0);
    textSize(32);
    textAlign(CENTER, CENTER);
    text("Game Over", width / 2, height / 2);
    textSize(16);
    text("Press R to Restart", width / 2, height / 2 + 50);
  }

  public void keyPressed() {
    if (!gameStarted && keyCode == ENTER) {
      gameStarted = true;
      return;
    }

    if (gameWon) {
      if (keyCode == ENTER) {
        currentLevel++;
        gameWon = false;
        initializeLevel();
      }
      return;
    }

    if (gameOver) {
      if (key == 'r' || key == 'R') {
        resetGame();
      }
      return;
    }

    if (keyCode == UP && isGrounded) {
      rectSpeedY = jumpStrength;
      isGrounded = false;
    } else if (keyCode == LEFT) {
      rectSpeedX = -5;
    } else if (keyCode == RIGHT) {
      rectSpeedX = 5;
    }
  }

  public void keyReleased() {
    if (keyCode == LEFT || keyCode == RIGHT) {
      rectSpeedX = 0;
    }
  }

  private void resetGame() {
    rectX = width / 2 - rectWidth / 2;
    rectY = height - rectHeight;
    rectSpeedX = 0;
    rectSpeedY = 0;
    score = 0;
    gameOver = false;
    gameWon = false;
    gameStarted = false;
    currentLevel = 0;
    initializeLevel();
  }

  public static void main(String[] args) {
    PApplet.main("Sketch");
  }

  // Define inner classes for game objects
  class Platform {
    float x, y, width, height;
    Platform(float x, float y, float width, float height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
    }
  }

  class MovingPlatform extends Platform {
    float speedX, speedY;
    MovingPlatform(float x, float y, float width, float height, float speedX, float speedY) {
      super(x, y, width, height);
      this.speedX = speedX;
      this.speedY = speedY;
    }
  }

  class Obstacle {
    float x, y, width, height;
    Obstacle(float x, float y, float width, float height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
    }
  }

  class Enemy extends Obstacle {
    float speedX, speedY;
    Enemy(float x, float y, float width, float height, float speedX, float speedY) {
      super(x, y, width, height);
      this.speedX = speedX;
      this.speedY = speedY;
    }
  }

  class Collectible {
    float x, y, size;
    Collectible(float x, float y, float size) {
      this.x = x;
      this.y = y;
      this.size = size;
    }
  }
}

