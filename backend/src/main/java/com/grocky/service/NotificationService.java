package com.grocky.service;

import com.grocky.entity.Order;
import com.grocky.entity.OrderStatus;
import com.grocky.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    @Async
    public void sendOrderConfirmation(Order order) {
        log.info("Sending order confirmation email for order: {}", order.getOrderNumber());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@grocky.com");
            message.setTo(order.getCustomer().getEmail());
            message.setSubject("Grocky - Order Confirmation #" + order.getOrderNumber());

            StringBuilder body = new StringBuilder();
            body.append("Hello ").append(order.getCustomer().getName()).append(",\n\n");
            body.append("Thank you for your order! We've received it and are processing it now.\n\n");
            body.append("Order Summary:\n");
            body.append("Order Number: ").append(order.getOrderNumber()).append("\n");
            body.append("Total Amount: $").append(order.getTotalAmount()).append("\n");
            body.append("Delivery to: ").append(order.getDeliveryAddress()).append("\n\n");
            body.append("Estimated Delivery: ").append(order.getAiPredictedDeliveryTime()).append("\n\n");
            body.append("Track your order in real-time on our website!\n\n");
            body.append("Enjoy your fresh groceries!\n");
            body.append("The Grocky Team\n");
            body.append("www.grocky.com");

            message.setText(body.toString());
            mailSender.send(message);
            log.info("Email sent successfully to {}", order.getCustomer().getEmail());
        } catch (Exception e) {
            log.error("Failed to send order confirmation email", e);
        }
    }

    @Async
    public void sendOrderStatusUpdate(Order order, OrderStatus previousStatus) {
        log.info("Sending order status update email for order: {}", order.getOrderNumber());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@grocky.com");
            message.setTo(order.getCustomer().getEmail());
            message.setSubject("Grocky - Order Status Update #" + order.getOrderNumber());

            StringBuilder body = new StringBuilder();
            body.append("Hello ").append(order.getCustomer().getName()).append(",\n\n");
            body.append("Your order status has been updated!\n\n");
            body.append("Order Number: ").append(order.getOrderNumber()).append("\n");
            body.append("Previous Status: ").append(previousStatus).append("\n");
            body.append("Current Status: ").append(order.getStatus()).append("\n");

            if (order.getStatus() == OrderStatus.SHIPPED) {
                body.append("\nYour order is on its way! Expected delivery: ")
                      .append(order.getScheduledDeliveryDate()).append("\n");
            } else if (order.getStatus() == OrderStatus.DELIVERED) {
                body.append("\nYour order has been delivered! We hope you enjoy your groceries.\n");
                body.append("Please consider leaving a review for the products you purchased.\n");
            }

            body.append("\nTrack your order: www.grocky.com/orders/").append(order.getId()).append("\n\n");
            body.append("The Grocky Team\n");
            body.append("www.grocky.com");

            message.setText(body.toString());
            mailSender.send(message);
            log.info("Status update email sent successfully to {}", order.getCustomer().getEmail());
        } catch (Exception e) {
            log.error("Failed to send status update email", e);
        }
    }

    @Async
    public void sendPaymentConfirmation(Order order, String transactionId) {
        log.info("Sending payment confirmation for order: {}", order.getOrderNumber());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("payments@grocky.com");
            message.setTo(order.getCustomer().getEmail());
            message.setSubject("Grocky - Payment Confirmation #" + order.getOrderNumber());

            StringBuilder body = new StringBuilder();
            body.append("Hello ").append(order.getCustomer().getName()).append(",\n\n");
            body.append("Your payment has been successfully processed!\n\n");
            body.append("Order Number: ").append(order.getOrderNumber()).append("\n");
            body.append("Transaction ID: ").append(transactionId).append("\n");
            body.append("Amount Paid: $").append(order.getTotalAmount()).append("\n");
            body.append("Payment Method: Credit/Debit Card\n\n");
            body.append("Thank you for shopping with Grocky!\n\n");
            body.append("The Grocky Team\n");
            body.append("www.grocky.com");

            message.setText(body.toString());
            mailSender.send(message);
            log.info("Payment confirmation sent successfully");
        } catch (Exception e) {
            log.error("Failed to send payment confirmation", e);
        }
    }

    @Async
    public void sendLowStockAlert(List<Product> products) {
        log.info("Sending low stock alert to admin for {} products", products.size());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("system@grocky.com");
            message.setTo("admin@grocky.com");
            message.setSubject("URGENT: Low Stock Alert - Grocky Inventory");

            StringBuilder body = new StringBuilder();
            body.append("The following products have reached or fallen below reorder levels:\n\n");
            for (Product p : products) {
                body.append("- ").append(p.getName())
                    .append(" (Stock: ").append(p.getStockQuantity())
                    .append(", Reorder Level: ").append(p.getReorderLevel()).append(")\n");
            }
            body.append("\nPlease review the admin dashboard for AI-powered reorder suggestions.\n\n");
            body.append("AI Demand Scores are available for each product.\n\n");
            body.append("Grocky Inventory Management System");

            message.setText(body.toString());
            mailSender.send(message);
            log.info("Low stock alert sent successfully");
        } catch (Exception e) {
            log.error("Failed to send low stock alert email", e);
        }
    }

    @Async
    public void sendWelcomeEmail(String customerEmail, String customerName) {
        log.info("Sending welcome email to: {}", customerEmail);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("welcome@grocky.com");
            message.setTo(customerEmail);
            message.setSubject("Welcome to Grocky - Fresh Groceries Delivered!");

            StringBuilder body = new StringBuilder();
            body.append("Welcome ").append(customerName).append("!\n\n");
            body.append("Thank you for joining Grocky! We're excited to bring fresh, quality groceries to your doorstep.\n\n");
            body.append("As a new member, you'll enjoy:\n");
            body.append("- Personalized AI-powered product recommendations\n");
            body.append("- Real-time order tracking\n");
            body.append("- Loyalty points on every purchase\n");
            body.append("- Fast and reliable delivery\n\n");
            body.append("Use code WELCOME50 for 50% off your first order!\n\n");
            body.append("Start shopping: www.grocky.com/products\n\n");
            body.append("The Grocky Team\n");
            body.append("www.grocky.com");

            message.setText(body.toString());
            mailSender.send(message);
            log.info("Welcome email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send welcome email", e);
        }
    }

    @Async
    public void sendPasswordResetEmail(String customerEmail, String resetToken) {
        log.info("Sending password reset email to: {}", customerEmail);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("security@grocky.com");
            message.setTo(customerEmail);
            message.setSubject("Grocky - Password Reset Request");

            StringBuilder body = new StringBuilder();
            body.append("Hello,\n\n");
            body.append("We received a request to reset your password.\n\n");
            body.append("Click the link below to reset your password:\n");
            body.append("www.grocky.com/reset-password?token=").append(resetToken).append("\n\n");
            body.append("This link will expire in 1 hour.\n\n");
            body.append("If you didn't request this, please ignore this email.\n\n");
            body.append("The Grocky Team\n");
            body.append("www.grocky.com");

            message.setText(body.toString());
            mailSender.send(message);
            log.info("Password reset email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send password reset email", e);
        }
    }
}
