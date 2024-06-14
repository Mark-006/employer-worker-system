package com.cbozan.main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.cbozan.dao.RegisterDAO;

public class Register extends JFrame {

    private static final long serialVersionUID = 1L;
    
    public static final int H_FRAME = 400;
    public static final int W_FRAME = 540;
    private JPanel contentPane;
    private JButton button_register, button_login;
    private JLabel label_username, label_email, label_phoneNumber, label_password, label_icon, label_errorText;
    private JTextField textField_username, textField_email, textField_phoneNumber;
    private JPasswordField passwordField_password;
    private Insets insets;
    String errorText = "Please fill in all fields";
    
    public Register() {
        super("Register");
        setIconImage(Toolkit.getDefaultToolkit().getImage("src\\icon\\Login_user_24.png"));
        //setResizable(false);
        setLayout(null);
        setSize(W_FRAME, H_FRAME);
        setLocationRelativeTo(null);
        setLocation(getX() - 80, getY() - 80);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        
        insets = this.getInsets();
        
        GUI();
    }
    
    private void GUI() {
        
        contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setBounds(insets.left, insets.top, W_FRAME - insets.left - insets.right, 
                H_FRAME - insets.bottom - insets.top);
    
        label_username = new JLabel("Username");
        label_username.setFont(new Font("Tahoma", Font.PLAIN, 14));
        label_username.setBounds(120, 120, 70, 20);
        contentPane.add(label_username);
        
        label_email = new JLabel("Email");
        label_email.setFont(label_username.getFont());
        label_email.setBounds(label_username.getX(), label_username.getY() + 40, 
                label_username.getWidth(), label_username.getHeight());
        contentPane.add(label_email);
        
        label_phoneNumber = new JLabel("Phone Number");
        label_phoneNumber.setFont(label_username.getFont());
        label_phoneNumber.setBounds(label_email.getX(), label_email.getY() + 40, 
                label_email.getWidth(), label_email.getHeight());
        contentPane.add(label_phoneNumber);
        
        label_password = new JLabel("Password");
        label_password.setFont(label_username.getFont());
        label_password.setBounds(label_phoneNumber.getX(), label_phoneNumber.getY() + 40, 
                label_phoneNumber.getWidth(), label_phoneNumber.getHeight());
        contentPane.add(label_password);
        
        textField_username = new JTextField();
        textField_username.setBounds(label_username.getX() + label_username.getWidth() + 30, 
                label_username.getY(), 120, label_username.getHeight());
        contentPane.add(textField_username);
        
        textField_email = new JTextField();
        textField_email.setBounds(textField_username.getX(), label_email.getY(), 
                120, label_email.getHeight());
        contentPane.add(textField_email);
        
        textField_phoneNumber = new JTextField();
        textField_phoneNumber.setBounds(textField_username.getX(), label_phoneNumber.getY(), 
                120, label_phoneNumber.getHeight());
        contentPane.add(textField_phoneNumber);
        
        passwordField_password = new JPasswordField();
        passwordField_password.setBounds(textField_username.getX(), label_password.getY(), 
                120, label_password.getHeight());
        contentPane.add(passwordField_password);
        
        button_register = new JButton("Register");
        button_register.setBounds(textField_username.getX() + 20, label_password.getY() + 60, 80, 22);
        button_register.setFocusPainted(false);
        button_register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
        
                if (textField_username.getText().equals("") || textField_email.getText().equals("") || 
                        textField_phoneNumber.getText().equals("") || String.valueOf(passwordField_password.getPassword()).equals("")) {
                    label_errorText.setText(errorText);
                } else {
                    label_errorText.setText("");
                    RegisterDAO registerDAO = new RegisterDAO();
                    if (registerDAO.registerUser(textField_username.getText(), 
                            textField_email.getText(), textField_phoneNumber.getText(), 
                            String.valueOf(passwordField_password.getPassword()),"")) {
                        JOptionPane.showMessageDialog(contentPane, "Registration successful. Please login.", "Register", 
                                JOptionPane.INFORMATION_MESSAGE);
                        
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Register.this.dispose();
                                new Login();
                            }
                        });
                        
                    } else {
                        label_errorText.setText("Registration failed. Try again.");
                    }
                }
            }
        });
        contentPane.add(button_register);
        
        button_login = new JButton("Login");
        button_login.setBounds(button_register.getX() + 100, button_register.getY(), 80, 22);
        button_login.setFocusPainted(false);
        button_login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Register.this.dispose();
                        new Login();
                    }
                });
            }
        });
        contentPane.add(button_login);
        
        label_icon = new JLabel(new ImageIcon("src\\icon\\Login_user_72.png"));
        label_icon.setBounds(textField_username.getX() + 20, textField_username.getY() - 100, 72, 72);
        contentPane.add(label_icon);
        
        label_errorText = new JLabel();
        label_errorText.setForeground(Color.RED);
        label_errorText.setBounds(button_register.getX() - 45, button_register.getY() + 30, 
                170, 30);
        label_errorText.setFont(new Font("Tahoma", Font.PLAIN + Font.BOLD, 11));
        contentPane.add(label_errorText);
        
        setContentPane(contentPane);
    }
}