import java.awt.event.*;

import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.*;

public class TankBattle01 {
	public static void main(String arg[]) {
		new FrameLaunch();
	}
}

class FrameLaunch {
	Label title_label = null;
	JFrame main_Frame = null;
	Button button_start = null;

	FrameLaunch() {
		main_Frame = new JFrame();
		main_Frame.setTitle("TankBattle_By:Lxa");
		main_Frame.setBounds(300, 300, 700, 700);
		main_Frame.setVisible(true);
		main_Frame.setLayout(null);
		main_Frame.setResizable(false);
		new Thread(new JPanelLaunch()).start();
		main_Frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				main_Frame.setVisible(false);
				System.exit(0);
			}
		});

	}

	class JPanelLaunch extends JPanel implements Runnable {
		Image boom1 = Toolkit.getDefaultToolkit().getImage("image/bomb_1.PNG");
		Image boom2 = Toolkit.getDefaultToolkit().getImage("image/bomb_2.PNG");
		Image boom3 = Toolkit.getDefaultToolkit().getImage("image/bomb_3.PNG");
		Image boom4 = Toolkit.getDefaultToolkit().getImage("image/bomb_4.PNG");
		Image boom5 = Toolkit.getDefaultToolkit().getImage("image/bomb_5.PNG");
		ArrayList picList = new ArrayList();

		boolean startFlag = false;
		boolean keyStart = false;
		boolean cleanTitle = false;
		boolean myTankAlive = true;
		boolean restartShowFlag = true;

		myTank my_tank = new myTank(0, 360);

		boolean moveFlag = true;

		// 定义数组 敌人 子弹.
		CopyOnWriteArrayList<enemyTank> enemy = new CopyOnWriteArrayList();
		CopyOnWriteArrayList myShot = new CopyOnWriteArrayList();
		CopyOnWriteArrayList enemyShot = new CopyOnWriteArrayList();

		JPanelLaunch() {
			picList.add(boom1);
			picList.add(boom2);
			picList.add(boom3);
			picList.add(boom4);
			picList.add(boom5);
			setBounds(0, 0, 700, 700);
			setVisible(true);
			setBackground(new Color(105, 105, 105));
			setLayout(null);
			addKeyListener(new keyMove());
			setFocusable(true);
			main_Frame.add(this);
			button_start = new Button("Start");
			button_start.setFont(new Font("Arial", Font.BOLD, 48));
			button_start.setBounds(200, 450, 300, 100);
			button_start.addActionListener(new cleanFrame());
			add(button_start);
			new Thread(new Runnable() {
				public void run() {
					new Thread(new startMusicStart()).start();
					String title = "Tank Battle";
					title_label = new Label();
					title_label.setBounds(100, 50, 500, 200);
					title_label.setForeground(new Color(253, 245, 230));
					add(title_label);
					title_label.setBackground(new Color(105, 105, 105));
					for (int i = 1; i < 88; i++) {
						title_label.setFont(new Font("Arial", Font.BOLD, i));
						title_label.setText(title);
						try {
							Thread.sleep(20);
						} catch (InterruptedException ae) {
							System.out.println("Sleep error!");
						}
						title_label.setText("");
					}
					title_label.setFont(new Font("Arial", Font.BOLD, 88));
					title_label.setText(title);
				}
			}).start();
			my_tank.setDir(1);
			for (int walk = 0; walk < 320; walk = walk + 5) {
				try {
					Thread.sleep(25);
					my_tank.setX(walk);
				} catch (InterruptedException ae) {
					ae.printStackTrace();
				}
				repaint();
			}
			// 调头 向上.
			my_tank.setDir(0);
			repaint();
			startFlag = true;
		}

		public void restartPanelShow() {
			Panel p_restart = new Panel();
			p_restart.setVisible(true);
			p_restart.setBounds(200, 200, 300, 300);
			p_restart.setLayout(null);
			p_restart.setBackground(new Color(176, 196, 222));
			Label l_restart = new Label("Wasted!");
			l_restart.setVisible(true);
			l_restart.setFont(new Font("Arial", 48, 48));
			l_restart.setBounds(50, 50, 200, 100);
			Button b_restart = new Button("Restart");
			b_restart.setVisible(true);
			b_restart.setFont(new Font("Arial", 48, 36));
			b_restart.setBounds(50, 150, 200, 100);
			b_restart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					myTankAlive = true;
					l_restart.setVisible(false);
					b_restart.setVisible(false);
					p_restart.setVisible(false);
				}
			});
			p_restart.add(l_restart);
			p_restart.add(b_restart);
			add(p_restart);
			restartShowFlag = false;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (myTankAlive == true) {
				drawTank(my_tank.getX(), my_tank.getY(), g, my_tank.getDir(), 0);
			} else if (myTankAlive == false) {
				if (restartShowFlag) {
					restartPanelShow();
				}
			}

			// paint敌人.并随机移动.
			Iterator<enemyTank> i = enemy.iterator();
			while (i.hasNext()) {
				// for(int enemyNum = 0 ;enemyNum<enemy.size();enemyNum++) {
				enemyTank et = i.next();
				// enemyTank et = enemy.get(enemyNum);
				if (et.isAlive) {
					int nowDir = et.getDir();
					boolean hitWall = false;
					et.setIsTouch(et.getX(), et.getY(), nowDir);
					switch (nowDir) {
					case 0:
						if (et.getY() <= 10) {
							hitWall = true;
							break;
						} else if (et.setIsTouch(et.getX(), et.getY(), nowDir)) {
							hitWall = true;
							break;
						} else {
							et.setY(et.getY() - 5);
						}
						break;
					case 1:
						if (et.getX() >= 630) {
							hitWall = true;
							break;
						} else if (et.setIsTouch(et.getX(), et.getY(), nowDir)) {
							hitWall = true;
							break;
						} else {
							et.setX(et.getX() + 5);
						}
						break;
					case 2:
						if (et.getY() >= 600) {
							hitWall = true;
							break;
						} else if (et.setIsTouch(et.getX(), et.getY(), nowDir)) {
							hitWall = true;
							break;
						} else {
							et.setY(et.getY() + 5);
						}
						break;
					case 3:
						if (et.getX() <= 10) {
							hitWall = true;
							break;
						} else if (et.setIsTouch(et.getX(), et.getY(), nowDir)) {
							hitWall = true;
							break;
						} else {
							et.setX(et.getX() - 5);
						}
						break;
					}
					if (hitWall) {
						int randDir = (int) Math.rint(Math.random() * 3);
						et.setDir(randDir);
					}
					if (moveFlag) {
						moveFlag = false;
					}
					if (et.isAlive) {
						drawTank(et.getX(), et.getY(), g, et.getDir(), 1);
					}
				}
			}

			try {
				Iterator<cannonball> ies = enemyShot.iterator();
				while (ies.hasNext()) {
					cannonball cb = ies.next();
					int thisDir = cb.getDir();
					switch (thisDir) {
					case 0:
						if (cb.getY() <= -1) {
							cb.setAliveFlag();
						} else {
							cb.setShotPoint(cb.getX(), cb.getY() - 8);
						}
						break;
					case 1:
						if (cb.getX() >= 795) {
							cb.setAliveFlag();
						} else {
							cb.setShotPoint(cb.getX() + 8, cb.getY());
						}
						break;
					case 2:
						if (cb.getY() >= 795) {
							cb.setAliveFlag();
						} else {
							cb.setShotPoint(cb.getX(), cb.getY() + 8);
						}
						break;
					case 3:
						if (cb.getX() <= -1) {
							cb.setAliveFlag();
						} else {
							cb.setShotPoint(cb.getX() - 8, cb.getY());
						}
						break;
					}

					/* 这个多线程remove会报错 很难受 干脆不删了 内存占点就占点吧 = =， */
					// enemyShot.removeAll(deletList);
					// 检测自己的tank是否被击中.
					switch (thisDir) {
					case 0:
					case 2:
						if (cb.getX() > my_tank.getX() && cb.getX() < my_tank.getX() + 40 && cb.getY() > my_tank.getY()
								&& cb.getY() < my_tank.getY() + 60) {
							myTankAlive = false;
							cb.setAliveFlag();
						}
						break;
					case 1:
					case 3:
						if (cb.getX() > my_tank.getX() && cb.getX() < my_tank.getX() + 60 && cb.getY() > my_tank.getY()
								&& cb.getY() < my_tank.getY() + 40) {
							myTankAlive = false;
							cb.setAliveFlag();
						}
					}

					if (cb.enemyShotAliveFlag) {
						drawBall(cb.getX(), cb.getY(), g);
					}
				}
			} catch (ConcurrentModificationException ae) {
				System.out.println("ConcurrentModificationException");
			}

			// 可以加载炮弹.
			// for(int myShotNum = 0 ;myShotNum<myShot.size();myShotNum++){
			// myCannonball cm = (myCannonball)myShot.get(myShotNum);
			Iterator icm = myShot.iterator();
			while (icm.hasNext()) {
				myCannonball cm = (myCannonball) icm.next();
				int thisDir = cm.getDir();
				switch (thisDir) {
				case 0:
					if (cm.getY() <= -1) {
						cm.setAliveFlag();
					} else {
						cm.setPoint(cm.getX(), cm.getY() - 8);
					}
					break;
				case 1:
					if (cm.getX() >= 795) {
						cm.setAliveFlag();
					} else {
						cm.setPoint(cm.getX() + 8, cm.getY());
					}
					break;
				case 2:
					if (cm.getY() >= 795) {
						cm.setAliveFlag();
					} else {
						cm.setPoint(cm.getX(), cm.getY() + 8);
					}
					break;
				case 3:
					if (cm.getX() <= -1) {
						cm.setAliveFlag();
					} else {
						cm.setPoint(cm.getX() - 8, cm.getY());
					}
					break;
				}
				Iterator<enemyTank> iHit = enemy.iterator();
				while (iHit.hasNext()) {
					enemyTank et = iHit.next();
					// for(int enemyTankNum = 0 ;enemyTankNum<enemy.size();enemyTankNum++) {
					// enemyTank et = (enemyTank)enemy.get(enemyTankNum);

					if (et.isAlive) {
						switch (thisDir) {
						case 0:
						case 2:
							if (cm.getX() > et.getX() && cm.getX() < et.getX() + 45 && cm.getY() > et.getY()
									&& cm.getY() < et.getY() + 65) {
								et.isAlive = false;
								for (int num = 0; num < picList.size(); num++) {
									Image pic = (Image) picList.get(num);
									System.out.println(cm.getX());
									g.drawImage(pic, et.getX() + 8, et.getY(), 40, 40, this);
								}
								cm.setAliveFlag();
								// Iterator<Image> iPic = picList.iterator();
								// System.out.println("!!!");
								// while(iPic.hasNext()) {
								// Image pic = iPic.next();

							}
							break;
						case 1:
						case 3:
							if (cm.getX() > et.getX() && cm.getX() < et.getX() + 65 && cm.getY() > et.getY()
									&& cm.getY() < et.getY() + 45) {
								et.isAlive = false;
								for (int num = 0; num < picList.size(); num++) {
									Image pic = (Image) picList.get(num);
									System.out.println(cm.getX());
									g.drawImage(pic, et.getX(), et.getY() - 10, 40, 40, this);
								}
								cm.setAliveFlag();
							}
						}
					}
				}
				if (cm.aliveFlag) {
					drawBall(cm.getX(), cm.getY(), g);
				}
			}
		}

		public void drawBall(int x, int y, Graphics g) {
			g.setColor(Color.yellow);
			g.fillRect(x, y, 2, 2);
		}

		public void drawTank(int x, int y, Graphics g, int direation, int type) {
			switch (type) {
			case 0:
				g.setColor(new Color(205, 92, 92));
				break;
			case 1:
				g.setColor(new Color(245, 245, 245));
			}
			switch (direation) {
			case 0:// 向上.
				g.fill3DRect(x, y, 10, 60, false);
				g.fill3DRect(x + 30, y, 10, 60, false);
				g.fill3DRect(x + 5, y + 10, 30, 40, false);
				g.fillOval(x + 10, y + 20, 20, 20);
				g.fillRect(x + 20, y - 5, 2, 35);
				break;
			case 1:// 向右.
				g.fill3DRect(x, y, 60, 10, false);
				// 画出下面的矩形
				g.fill3DRect(x, y + 30, 60, 10, false);
				// 画出中间的矩形
				g.fill3DRect(x + 10, y + 5, 40, 30, false);
				// 画出圆形
				g.fillOval(x + 15, y + 10, 20, 20);
				// 画炮管
				g.fillRect(x + 30, y + 19, 35, 2);
				break;

			case 2:// 向下.
				g.fill3DRect(x, y, 10, 60, false);
				g.fill3DRect(x + 30, y, 10, 60, false);
				g.fill3DRect(x + 5, y + 8, 30, 40, false);
				g.fillOval(x + 10, y + 13, 20, 20);
				g.fillRect(x + 20, y + 30, 2, 35);
				break;
			case 3:// 向左.
				g.fill3DRect(x, y, 60, 10, false);
				g.fill3DRect(x, y + 30, 60, 10, false);
				g.fill3DRect(x + 10, y + 5, 40, 30, false);
				g.fillOval(x + 20, y + 10, 20, 20);
				g.fillRect(x - 5, y + 19, 35, 2);
				break;
			}
		}

		public void run() {
			for (;;) {
				try {
					Thread.sleep(50);
					// 可以运行.
				} catch (InterruptedException aeInterrupted) {
					aeInterrupted.printStackTrace();
				}
				repaint();
			}
		}

		public void gameLaunch() {
			for (int i = 0; i < 10; i++) {
				if (i < 5) {
					enemyTank enemy_Tank = new enemyTank(80 + (i * 120), 100);
					enemy.add(enemy_Tank);
				} else {
					enemyTank enemy_Tank = new enemyTank(80 + ((i - 5) * 120), 550);
					enemy.add(enemy_Tank);
				}
			}
			new Thread(new enemyMove()).start();
			new Thread(new enemyShot()).start();
			new Thread(new Runnable() {
				// 检测是否成功.
				public void run() {
					while (true) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException ae) {
							ae.printStackTrace();
						}
						int num = 0;
						for (int enemyNum = 0; enemyNum < enemy.size(); enemyNum++) {
							enemyTank etNum = enemy.get(enemyNum);
							if (etNum.isAlive == false) {
								num++;
							}
						}
						if (num == enemy.size()) {
							JOptionPane.showMessageDialog(null, "成功！", "提示", JOptionPane.ERROR_MESSAGE);
							System.exit(0);
						}
					}
				}
			}).start();
		}

		class enemyMove implements Runnable {
			public void run() {
				Iterator<enemyTank> i = enemy.iterator();
				while (i.hasNext()) {
					enemyTank et = i.next();
					if (et.isAlive) {
						new Thread(new Runnable() {
							public void run() {
								while (true) {
									int randDir = (int) Math.rint(Math.random() * 3);
									et.setDir(randDir);
									try {
										Thread.sleep((int) Math.rint(Math.random() * 6000));
									} catch (InterruptedException ae) {
										System.out.println("Thread error!");
									}
								}
							}
						}).start();

					}
				}

			}
		}

		class cannonball {
			int dir;
			int x;
			int y;
			boolean enemyShotAliveFlag = true;

			cannonball(int dir) {
				this.dir = dir;
			}

			public void setShotPoint(int x, int y) {
				this.x = x;
				this.y = y;
			}

			public int getX() {
				return x;
			}

			public int getY() {
				return y;
			}

			public int getDir() {
				return dir;
			}

			public void setAliveFlag() {
				enemyShotAliveFlag = false;
			}
		}

		class myCannonball extends cannonball {
			int dir;
			int x;
			int y;
			boolean aliveFlag = true;

			myCannonball(int dir) {
				super(dir);
				this.dir = dir;
			}

			public void setPoint(int x, int y) {
				this.x = x;
				this.y = y;
			}

			public int getX() {
				return x;
			}

			public int getY() {
				return y;
			}

			public void setAliveFlag() {
				aliveFlag = false;
			}
		}

		class enemyShot implements Runnable {
			public void run() {
				while (true) {
					Iterator<enemyTank> i = enemy.iterator();
					enemyTank et = null;
					try {
						Thread.sleep((int) Math.rint(Math.random() * 3000));
					} catch (InterruptedException ae) {
						ae.printStackTrace();
					}
					for (int enemyNum = 0; enemyNum < enemy.size(); enemyNum++) {
						et = enemy.get(enemyNum);
						if (et.isAlive) {
							int enemyDir = et.getDir();
							switch (enemyDir) {
							case 0:
								cannonball cb0 = new cannonball(0);
								cb0.setShotPoint(et.getX() + 20, et.getY() - 2);
								enemyShot.add(cb0);
								break;
							case 1:
								cannonball cb1 = new cannonball(1);
								cb1.setShotPoint(et.getX() + 60, et.getY() + 19);
								enemyShot.add(cb1);
								break;
							case 2:
								cannonball cb2 = new cannonball(2);
								cb2.setShotPoint(et.getX() + 20, et.getY() + 62);
								enemyShot.add(cb2);
								break;
							case 3:
								cannonball cb3 = new cannonball(3);
								cb3.setShotPoint(et.getX() - 2, et.getY() + 19);
								enemyShot.add(cb3);
							}
						}
					}
					// while(i.hasNext()) {
					// enemyTank et = i.next();
					//
					// }
				}
			}
		}

		public void removeTitle() {
			this.remove(title_label);
			this.remove(button_start);
		}

		class keyMove extends KeyAdapter {
			public void keyPressed(KeyEvent e) {
				if (keyStart == true) {
					if (e.getKeyCode() == KeyEvent.VK_W) {
						System.out.println("Up");
						my_tank.setDir(0);
						my_tank.setY(my_tank.getY() - 5);
					} else if (e.getKeyCode() == KeyEvent.VK_S) {
						System.out.println("Down");
						my_tank.setDir(2);
						my_tank.setY(my_tank.getY() + 5);
					} else if (e.getKeyCode() == KeyEvent.VK_A) {
						System.out.println("Left");
						my_tank.setDir(3);
						my_tank.setX(my_tank.getX() - 5);
					} else if (e.getKeyCode() == KeyEvent.VK_D) {
						System.out.println("right");
						my_tank.setDir(1);
						my_tank.setX(my_tank.getX() + 5);
					} else if (e.getKeyCode() == KeyEvent.VK_J) {
						System.out.println("shot!");
						int myDir = my_tank.getDir();
						switch (myDir) {
						case 0:
							myCannonball cm0 = new myCannonball(0);
							cm0.setPoint(my_tank.getX() + 20, my_tank.getY() - 2);
							myShot.add(cm0);
							break;
						case 1:
							myCannonball cm1 = new myCannonball(1);
							cm1.setPoint(my_tank.getX() + 60, my_tank.getY() + 19);
							myShot.add(cm1);
							break;
						case 2:
							myCannonball cm2 = new myCannonball(2);
							cm2.setPoint(my_tank.getX() + 20, my_tank.getY() + 62);
							myShot.add(cm2);
							break;
						case 3:
							myCannonball cm3 = new myCannonball(3);
							cm3.setPoint(my_tank.getX() - 2, my_tank.getY() + 19);
							myShot.add(cm3);
							break;
						}

					}
					// repaint();
				}
			}
		}

		class Tank {
			int x = 0;
			int y = 0;
			boolean isAlive = true;

			Tank(int x, int y) {
				this.x = x;
				this.y = y;
			}

			public void dead() {
				isAlive = false;
			}

			public boolean isAlive() {
				return isAlive;
			}

			public int getX() {
				return x;
			}

			public int getY() {
				return y;
			}

			public void setX(int x) {
				this.x = x;
			}

			public void setY(int y) {
				this.y = y;
			}

		}

		class myTank {
			int x;
			int y;
			int dir;
			boolean isAlive = true;

			public void dead() {
				isAlive = false;
			}

			public boolean isAlive() {
				return isAlive;
			}

			public int getX() {
				return x;
			}

			public int getY() {
				return y;
			}

			public void setX(int x) {
				this.x = x;
			}

			public void setY(int y) {
				this.y = y;
			}

			myTank(int x, int y) {
				this.x = x;
				this.y = y;
			}

			public int getDir() {
				return dir;
			}

			public void setDir(int dir) {
				this.dir = dir;
			}
		}

		class enemyTank extends Tank {
			int x;
			int y;
			int dir = 2;

			public enemyTank(int x, int y) {
				super(x, y);
				this.x = x;
				this.y = y;
			}

			public int getDir() {
				return dir;
			}

			public void setDir(int dir) {
				this.dir = dir;
			}

			public boolean setIsTouch(int x, int y, int dir) {
				switch (dir) {
				case 0:
					// 我的坦克向上
					// 取出所有的敌人坦克
					for (int i = 0; i < enemy.size(); i++) {
						// 取出第一个坦克
						enemyTank et = enemy.get(i);
						// 如果不是自己
						if (et != this) {
							// 如果敌人的方向是向下或者向上
							if (et.dir == 0 || et.dir == 1) {
								if (x >= et.x && x <= et.x + 40 && y >= et.y && y <= et.y + 60) {
									return true;
								}
								if (x + 40 >= et.x && x + 40 <= et.x + 40 && y >= et.y && y <= et.y + 60) {
									return true;
								}
							}
							if (et.dir == 3 || et.dir == 2) {
								if (x >= et.x && x <= et.x + 60 && y >= et.y && y <= et.y + 40) {
									return true;
								}
								if (x + 40 >= et.x && x + 40 <= et.x + 60 && y >= et.y && y <= et.y + 40) {
									return true;
								}
							}
						}
					}
					break;
				case 1:
					// 我的坦克向下
					// 取出所有的敌人坦克
					for (int i = 0; i < enemy.size(); i++) {
						// 取出第一个坦克
						enemyTank et = enemy.get(i);
						// 如果不是自己
						if (et != this) {
							// 如果敌人的方向是向下或者向上
							if (et.dir == 0 || et.dir == 1) {
								if (x >= et.x && x <= et.x + 40 && y + 60 >= et.y && y + 60 <= et.y + 60) {
									return true;
								}
								if (x + 40 >= et.x && x + 40 <= et.x + 40 && y + 60 >= et.y && y + 60 <= et.y + 60) {
									return true;
								}
							}
							if (et.dir == 3 || et.dir == 2) {
								if (x >= et.x && x <= et.x + 60 && y + 60 >= et.y && y + 60 <= et.y + 40) {
									return true;
								}
								if (x + 40 >= et.x && x + 40 <= et.x + 60 && y + 60 >= et.y && y + 60 <= et.y + 40) {
									return true;
								}
							}
						}
					}

					break;
				case 2:
					// 我的坦克向左
					// 取出所有的敌人坦克
					for (int i = 0; i < enemy.size(); i++) {
						// 取出第一个坦克
						enemyTank et = enemy.get(i);
						// 如果不是自己
						if (et != this) {
							// 如果敌人的方向是向下或者向上
							if (et.dir == 0 || et.dir == 1) {
								if (x >= et.x && x <= et.x + 40 && y >= et.y && y <= et.y + 60) {
									return true;
								}
								if (x >= et.x && x <= et.x + 40 && y + 40 >= et.y && y + 40 <= et.y + 60) {
									return true;
								}
							}
							if (et.dir == 3 || et.dir == 2) {
								if (x >= et.x && x <= et.x + 60 && y >= et.y && y <= et.y + 40) {
									return true;
								}
								if (x >= et.x && x <= et.x + 60 && y + 40 >= et.y && y + 40 <= et.y + 40) {
									return true;
								}
							}
						}
					}

					break;
				case 3:
					// 坦克向右
					for (int i = 0; i < enemy.size(); i++) {
						// 取出第一个坦克
						enemyTank et = enemy.get(i);
						// 如果不是自己
						if (et != this) {
							// 如果敌人的方向是向下或者向上
							if (et.dir == 0 || et.dir == 1) {
								if (x + 60 >= et.x && x + 60 <= et.x + 40 && y >= et.y && y <= et.y + 60) {
									return true;
								}
								if (x + 60 >= et.x && x + 60 <= et.x + 40 && y + 40 >= et.y && y + 40 <= et.y + 60) {
									return true;
								}
							}
							if (et.dir == 2 || et.dir == 3) {
								if (x + 60 >= et.x && x + 60 <= et.x + 60 && y >= et.y && y <= et.y + 40) {
									return true;
								}
								if (x + 60 >= et.x && x + 60 <= et.x + 60 && y + 40 >= et.y && y + 40 <= et.y + 40) {
									return true;
								}
							}
						}
					}
					break;
				default:
				}
				return false;
			}
		}

		class cleanFrame implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if (startFlag == true) {
					removeTitle();
					// 清理界面.
					keyStart = true;
					cleanTitle = true;
					// 可以运行.
					gameLaunch();
				}
			}
		}

	}

	class startMusicStart implements Runnable {
		public void run() {
			try {
				File musicFile = new File("C:\\ForJava\\JavaWorkSpace\\TankBattle\\music\\TankBattle.wav");
				URL music = musicFile.toURL();
				AudioClip Sound = Applet.newAudioClip(music);
				Sound.play();
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
				}
			} catch (MalformedURLException ae) {
				System.out.println("Sound play error!");
			}
		}
	}
}
