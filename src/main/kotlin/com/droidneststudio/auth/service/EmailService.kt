package com.droidneststudio.auth.service

import org.apache.commons.mail.HtmlEmail
import org.slf4j.LoggerFactory

class EmailService(
    private val host: String,
    private val port: Int,
    private val username: String,
    private val password: String,
    private val fromEmail: String,
    private val fromName: String
) {
    private val logger = LoggerFactory.getLogger(EmailService::class.java)

    // Brand settings
    private val brandColor = "#6366F1"
    private val logoUrl = "https://scontent.fcgp3-2.fna.fbcdn.net/v/t39.30808-6/618375739_1554124869141000_7314522341546055519_n.jpg?_nc_cat=110&ccb=1-7&_nc_sid=6ee11a&_nc_eui2=AeEzzH4_vTToDoWLB6nKs89TQlE7ETRs0xNCUTsRNGzTE0lOE0okMWF8PHLVJEvZssuIHVTep9E1UklmXxIgSctk&_nc_ohc=t3AMgA-VlQcQ7kNvwE1hJmL&_nc_oc=AdkWu_mn1SWL3olEljWTXpt3Cn8WKYwBekl0jG8FDpU5ObiZhVtGNwq9R78_KdNyiTo&_nc_zt=23&_nc_ht=scontent.fcgp3-2.fna&_nc_gid=Ieh9YwYWMRD3C_5UkIyZ8w&oh=00_AfuUi4oVxAQtnfJw-s5xy8N93E_kVT2q_1LZr-zvV1zCUg&oe=6987F265"
    private val websiteUrl = "https://droidneststudio.com"

    fun sendOtpEmail(to: String, otp: String, subject: String): Boolean {
        return try {
            val html = buildOtpEmailTemplate(otp, subject)
            sendHtmlEmail(to, subject, html)
        } catch (e: Exception) {
            logger.error("Failed to send email to $to: ${e.message}")
            false
        }
    }

    private fun buildOtpEmailTemplate(otp: String, subject: String): String {
        val otpDigits = otp.toCharArray().joinToString("") { digit ->
            """
            <td style="padding: 0 4px;">
                <div style="
                    width: 52px;
                    height: 68px;
                    background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
                    border: 2px solid #e2e8f0;
                    border-radius: 14px;
                    font-family: 'SF Mono', 'Fira Code', 'Consolas', monospace;
                    font-size: 32px;
                    font-weight: 800;
                    color: #0f172a;
                    line-height: 68px;
                    text-align: center;
                    box-shadow: 
                        0 4px 6px -1px rgba(0, 0, 0, 0.07),
                        0 2px 4px -1px rgba(0, 0, 0, 0.04),
                        inset 0 2px 4px rgba(255, 255, 255, 0.8);
                ">$digit</div>
            </td>
            """.trimIndent()
        }

        return """
<!DOCTYPE html>
<html lang="en" xmlns:v="urn:schemas-microsoft-com:vml">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="x-apple-disable-message-reformatting">
    <meta name="color-scheme" content="light dark">
    <meta name="supported-color-schemes" content="light dark">
    <title>$subject</title>
    <!--[if mso]>
    <noscript>
        <xml>
            <o:OfficeDocumentSettings>
                <o:PixelsPerInch>96</o:PixelsPerInch>
            </o:OfficeDocumentSettings>
        </xml>
    </noscript>
    <![endif]-->
    <style>
        :root { color-scheme: light dark; supported-color-schemes: light dark; }
        @media (prefers-color-scheme: dark) {
            .dark-bg { background-color: #1a1a2e !important; }
            .dark-card { background-color: #16213e !important; }
            .dark-text { color: #e2e8f0 !important; }
        }
    </style>
</head>
<body style="margin: 0; padding: 0; background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%); font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; -webkit-font-smoothing: antialiased;">
    
    <!-- Hidden Preview Text -->
    <div style="display: none; font-size: 1px; color: #667eea; line-height: 1px; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden;">
        🔐 Your verification code is $otp — Valid for 10 minutes only. Don't share this code with anyone.
        &#847; &#847; &#847; &#847; &#847; &#847; &#847; &#847; &#847; &#847; &#847; &#847; &#847; &#847; &#847;
    </div>
    
    <!-- Wrapper Table -->
    <table role="presentation" cellpadding="0" cellspacing="0" width="100%" style="min-height: 100vh;">
        <tr>
            <td align="center" style="padding: 40px 16px;">
                
                <!-- Main Card -->
                <table role="presentation" cellpadding="0" cellspacing="0" width="100%" style="max-width: 440px;">
                    
                    <!-- Floating Logo/Brand -->
                    <tr>
                        <td align="center" style="padding-bottom: 24px;">
                            <table role="presentation" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td style="
                                        background: rgba(255, 255, 255, 0.95);
                                        backdrop-filter: blur(20px);
                                        -webkit-backdrop-filter: blur(20px);
                                        border-radius: 16px;
                                        padding: 12px 24px;
                                        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
                                    ">
                                        <span style="font-size: 18px; font-weight: 700; color: #0f172a; letter-spacing: -0.5px;">
                                            🛡️ $fromName
                                        </span>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    
                    <!-- Card Container -->
                    <tr>
                        <td>
                            <table role="presentation" cellpadding="0" cellspacing="0" width="100%" style="
                                background: #ffffff;
                                border-radius: 24px;
                                box-shadow: 
                                    0 25px 50px -12px rgba(0, 0, 0, 0.25),
                                    0 0 0 1px rgba(255, 255, 255, 0.1);
                                overflow: hidden;
                            ">
                                
                                <!-- Decorative Top Bar -->
                                <tr>
                                    <td style="height: 6px; background: linear-gradient(90deg, #667eea 0%, #764ba2 25%, #f093fb 50%, #f5576c 75%, #f093fb 100%); background-size: 200% 100%;"></td>
                                </tr>
                                
                                <!-- Header Section -->
                                <tr>
                                    <td style="padding: 48px 40px 32px; text-align: center;">
                                        
                                        <!-- Animated Icon Container -->
                                        <div style="
                                            width: 88px;
                                            height: 88px;
                                            margin: 0 auto 24px;
                                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                            border-radius: 24px;
                                            position: relative;
                                            box-shadow: 
                                                0 20px 40px -10px rgba(102, 126, 234, 0.5),
                                                0 4px 12px rgba(0, 0, 0, 0.1);
                                            transform: rotate(-3deg);
                                        ">
                                            <div style="
                                                position: absolute;
                                                top: 50%;
                                                left: 50%;
                                                transform: translate(-50%, -50%) rotate(3deg);
                                                font-size: 40px;
                                                line-height: 1;
                                            ">🔑</div>
                                            
                                            <!-- Decorative Dots -->
                                            <div style="
                                                position: absolute;
                                                top: -8px;
                                                right: -8px;
                                                width: 24px;
                                                height: 24px;
                                                background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
                                                border-radius: 50%;
                                                border: 3px solid #ffffff;
                                                box-shadow: 0 4px 12px rgba(240, 147, 251, 0.4);
                                            "></div>
                                        </div>
                                        
                                        <!-- Title -->
                                        <h1 style="
                                            margin: 0 0 12px;
                                            font-size: 28px;
                                            font-weight: 800;
                                            color: #0f172a;
                                            letter-spacing: -1px;
                                            line-height: 1.2;
                                        ">Verification Code</h1>
                                        
                                        <p style="
                                            margin: 0;
                                            font-size: 15px;
                                            color: #64748b;
                                            line-height: 1.6;
                                        ">
                                            Enter this code to verify your identity
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- OTP Display Section -->
                                <tr>
                                    <td style="padding: 0 32px 40px;">
                                        
                                        <!-- OTP Container -->
                                        <div style="
                                            background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
                                            border: 1px solid #e2e8f0;
                                            border-radius: 20px;
                                            padding: 32px 24px;
                                            text-align: center;
                                        ">
                                            <!-- Individual Digit Boxes -->
                                            <table role="presentation" cellpadding="0" cellspacing="0" style="margin: 0 auto;">
                                                <tr>
                                                    $otpDigits
                                                </tr>
                                            </table>
                                            
                                            <!-- Copy Hint -->
                                            <p style="
                                                margin: 20px 0 0;
                                                font-size: 13px;
                                                color: #94a3b8;
                                            ">
                                                📋 Tap to copy code
                                            </p>
                                        </div>
                                    </td>
                                </tr>
                                
                                <!-- Timer Section -->
                                <tr>
                                    <td style="padding: 0 32px 32px;">
                                        <table role="presentation" cellpadding="0" cellspacing="0" width="100%" style="
                                            background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
                                            border-radius: 16px;
                                            overflow: hidden;
                                        ">
                                            <tr>
                                                <td width="4" style="background: linear-gradient(180deg, #f59e0b 0%, #d97706 100%);"></td>
                                                <td style="padding: 16px 20px;">
                                                    <table role="presentation" cellpadding="0" cellspacing="0">
                                                        <tr>
                                                            <td style="padding-right: 12px; vertical-align: middle;">
                                                                <div style="
                                                                    width: 36px;
                                                                    height: 36px;
                                                                    background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
                                                                    border-radius: 10px;
                                                                    text-align: center;
                                                                    line-height: 36px;
                                                                    font-size: 18px;
                                                                ">⏱️</div>
                                                            </td>
                                                            <td style="vertical-align: middle;">
                                                                <p style="margin: 0; font-size: 14px; font-weight: 600; color: #92400e;">
                                                                    Code expires in <span style="color: #b45309;">10 minutes</span>
                                                                </p>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                
                                <!-- Security Tips -->
                                <tr>
                                    <td style="padding: 0 32px 40px;">
                                        <div style="
                                            background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
                                            border-radius: 16px;
                                            padding: 20px;
                                        ">
                                            <table role="presentation" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td style="padding-right: 14px; vertical-align: top;">
                                                        <div style="
                                                            width: 32px;
                                                            height: 32px;
                                                            background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
                                                            border-radius: 8px;
                                                            text-align: center;
                                                            line-height: 32px;
                                                            font-size: 16px;
                                                        ">🔒</div>
                                                    </td>
                                                    <td style="vertical-align: top;">
                                                        <p style="margin: 0 0 4px; font-size: 13px; font-weight: 700; color: #1e40af;">Security Notice</p>
                                                        <p style="margin: 0; font-size: 13px; color: #3b82f6; line-height: 1.5;">
                                                            Never share this code. We'll never ask for it via call or message.
                                                        </p>
                                                    </td>
                                                </tr>
                                            </table>
                                        </div>
                                    </td>
                                </tr>
                                
                                <!-- Divider with Icon -->
                                <tr>
                                    <td style="padding: 0 40px;">
                                        <table role="presentation" cellpadding="0" cellspacing="0" width="100%">
                                            <tr>
                                                <td style="border-bottom: 1px solid #e2e8f0;"></td>
                                                <td style="padding: 0 16px;">
                                                    <div style="
                                                        width: 32px;
                                                        height: 32px;
                                                        background: #f8fafc;
                                                        border: 1px solid #e2e8f0;
                                                        border-radius: 50%;
                                                        text-align: center;
                                                        line-height: 32px;
                                                        font-size: 14px;
                                                    ">💌</div>
                                                </td>
                                                <td style="border-bottom: 1px solid #e2e8f0;"></td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="padding: 32px 40px; text-align: center;">
                                        <p style="margin: 0 0 8px; font-size: 13px; color: #94a3b8;">
                                            Didn't request this code?
                                        </p>
                                        <p style="margin: 0; font-size: 13px; color: #cbd5e1;">
                                            You can safely ignore this email
                                        </p>
                                    </td>
                                </tr>
                                
                            </table>
                        </td>
                    </tr>
                    
                    <!-- Bottom Info -->
                    <tr>
                        <td style="padding: 32px 24px; text-align: center;">
                            <p style="margin: 0 0 8px; font-size: 12px; color: rgba(255, 255, 255, 0.9);">
                                Sent with ❤️ by <strong>$fromName</strong>
                            </p>
                            <p style="margin: 0; font-size: 11px; color: rgba(255, 255, 255, 0.6);">
                                © ${java.time.Year.now().value} $fromName. All rights reserved.
                            </p>
                        </td>
                    </tr>
                    
                </table>
                
            </td>
        </tr>
    </table>
    
</body>
</html>
        """.trimIndent()
    }

    fun sendWelcomeEmail(to: String, name: String): Boolean {
        return try {
            val html = buildWelcomeEmailTemplate(name)
            sendHtmlEmail(to, "Welcome to $fromName! 🎉", html)
        } catch (e: Exception) {
            logger.error("Failed to send welcome email to $to: ${e.message}")
            false
        }
    }

    private fun buildWelcomeEmailTemplate(name: String): String {
        return """
<!DOCTYPE html>
<html lang="en" xmlns:v="urn:schemas-microsoft-com:vml">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="x-apple-disable-message-reformatting">
    <meta name="color-scheme" content="light dark">
    <title>Welcome to $fromName</title>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');
        * { font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
    </style>
</head>
<body style="margin: 0; padding: 0; background: #0f0f23; font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; -webkit-font-smoothing: antialiased;">
    
    <!-- Preview Text -->
    <div style="display: none; font-size: 1px; line-height: 1px; max-height: 0; max-width: 0; opacity: 0; overflow: hidden;">
        🎉 Welcome aboard, $name! Your journey starts now. Discover amazing features waiting for you.
    </div>
    
    <!-- Background Pattern Wrapper -->
    <table role="presentation" cellpadding="0" cellspacing="0" width="100%" style="
        background: linear-gradient(180deg, #0f0f23 0%, #1a1a3e 50%, #0f0f23 100%);
        background-image: 
            radial-gradient(circle at 20% 20%, rgba(99, 102, 241, 0.15) 0%, transparent 50%),
            radial-gradient(circle at 80% 80%, rgba(236, 72, 153, 0.15) 0%, transparent 50%),
            radial-gradient(circle at 50% 50%, rgba(139, 92, 246, 0.1) 0%, transparent 70%);
    ">
        <tr>
            <td align="center" style="padding: 48px 16px;">
                
                <!-- Main Container -->
                <table role="presentation" cellpadding="0" cellspacing="0" width="100%" style="max-width: 520px;">
                    
                    <!-- Floating Header -->
                    <tr>
                        <td align="center" style="padding-bottom: 32px;">
                            <table role="presentation" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td style="
                                        background: rgba(255, 255, 255, 0.05);
                                        backdrop-filter: blur(20px);
                                        border: 1px solid rgba(255, 255, 255, 0.1);
                                        border-radius: 100px;
                                        padding: 12px 28px;
                                    ">
                                        <span style="font-size: 16px; font-weight: 600; color: #ffffff; letter-spacing: -0.3px;">
                                            ✨ $fromName
                                        </span>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    
                    <!-- Main Card -->
                    <tr>
                        <td>
                            <table role="presentation" cellpadding="0" cellspacing="0" width="100%" style="
                                background: linear-gradient(180deg, rgba(255,255,255,0.08) 0%, rgba(255,255,255,0.02) 100%);
                                backdrop-filter: blur(40px);
                                border: 1px solid rgba(255, 255, 255, 0.1);
                                border-radius: 32px;
                                overflow: hidden;
                            ">
                                
                                <!-- Hero Section -->
                                <tr>
                                    <td style="padding: 56px 40px 40px; text-align: center; position: relative;">
                                        
                                        <!-- Celebration Badge -->
                                        <table role="presentation" cellpadding="0" cellspacing="0" style="margin: 0 auto 28px;">
                                            <tr>
                                                <td style="
                                                    background: linear-gradient(135deg, rgba(34, 197, 94, 0.2) 0%, rgba(16, 185, 129, 0.2) 100%);
                                                    border: 1px solid rgba(34, 197, 94, 0.3);
                                                    border-radius: 100px;
                                                    padding: 10px 24px;
                                                ">
                                                    <span style="font-size: 13px; font-weight: 600; color: #4ade80; letter-spacing: 0.5px;">
                                                        ✓ ACCOUNT VERIFIED
                                                    </span>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <!-- Avatar/Icon -->
                                        <div style="
                                            width: 100px;
                                            height: 100px;
                                            margin: 0 auto 28px;
                                            background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #ec4899 100%);
                                            border-radius: 28px;
                                            position: relative;
                                            box-shadow: 
                                                0 0 60px rgba(99, 102, 241, 0.4),
                                                0 0 100px rgba(139, 92, 246, 0.2),
                                                0 20px 40px rgba(0, 0, 0, 0.3);
                                            transform: rotate(-6deg);
                                        ">
                                            <div style="
                                                position: absolute;
                                                top: 50%;
                                                left: 50%;
                                                transform: translate(-50%, -50%) rotate(6deg);
                                                font-size: 48px;
                                                line-height: 1;
                                            ">🎊</div>
                                            
                                            <!-- Sparkle Decorations -->
                                            <div style="
                                                position: absolute;
                                                top: -12px;
                                                right: -12px;
                                                font-size: 24px;
                                            ">⭐</div>
                                            <div style="
                                                position: absolute;
                                                bottom: -8px;
                                                left: -8px;
                                                font-size: 20px;
                                            ">✨</div>
                                        </div>
                                        
                                        <!-- Welcome Title -->
                                        <h1 style="
                                            margin: 0 0 16px;
                                            font-size: 36px;
                                            font-weight: 800;
                                            background: linear-gradient(135deg, #ffffff 0%, #a5b4fc 50%, #f0abfc 100%);
                                            -webkit-background-clip: text;
                                            -webkit-text-fill-color: transparent;
                                            background-clip: text;
                                            letter-spacing: -1.5px;
                                            line-height: 1.1;
                                        ">Welcome, $name!</h1>
                                        
                                        <p style="
                                            margin: 0;
                                            font-size: 16px;
                                            color: rgba(255, 255, 255, 0.6);
                                            line-height: 1.6;
                                        ">
                                            Your account is all set up and ready to go! 🚀
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Divider with Glow -->
                                <tr>
                                    <td style="padding: 0 40px;">
                                        <div style="
                                            height: 1px;
                                            background: linear-gradient(90deg, transparent 0%, rgba(99, 102, 241, 0.5) 50%, transparent 100%);
                                        "></div>
                                    </td>
                                </tr>
                                
                                <!-- Quick Start Section -->
                                <tr>
                                    <td style="padding: 40px;">
                                        
                                        <h2 style="
                                            margin: 0 0 24px;
                                            font-size: 14px;
                                            font-weight: 600;
                                            color: rgba(255, 255, 255, 0.4);
                                            letter-spacing: 1px;
                                            text-transform: uppercase;
                                        ">🎯 Quick Start Guide</h2>
                                        
                                        <!-- Feature Card 1 -->
                                        <table role="presentation" cellpadding="0" cellspacing="0" width="100%" style="margin-bottom: 16px;">
                                            <tr>
                                                <td style="
                                                    background: linear-gradient(135deg, rgba(34, 197, 94, 0.1) 0%, rgba(34, 197, 94, 0.05) 100%);
                                                    border: 1px solid rgba(34, 197, 94, 0.2);
                                                    border-radius: 20px;
                                                    padding: 20px;
                                                ">
                                                    <table role="presentation" cellpadding="0" cellspacing="0" width="100%">
                                                        <tr>
                                                            <td width="60" style="vertical-align: top;">
                                                                <div style="
                                                                    width: 48px;
                                                                    height: 48px;
                                                                    background: linear-gradient(135deg, #22c55e 0%, #10b981 100%);
                                                                    border-radius: 14px;
                                                                    text-align: center;
                                                                    line-height: 48px;
                                                                    font-size: 24px;
                                                                    box-shadow: 0 8px 20px rgba(34, 197, 94, 0.3);
                                                                ">👤</div>
                                                            </td>
                                                            <td style="vertical-align: top; padding-left: 16px;">
                                                                <h3 style="margin: 0 0 6px; font-size: 16px; font-weight: 700; color: #ffffff;">Complete Your Profile</h3>
                                                                <p style="margin: 0; font-size: 14px; color: rgba(255, 255, 255, 0.5); line-height: 1.5;">Add a photo and bio to personalize your experience</p>
                                                            </td>
                                                            <td width="32" style="vertical-align: middle; text-align: right;">
                                                                <span style="font-size: 18px; color: rgba(255, 255, 255, 0.3);">→</span>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <!-- Feature Card 2 -->
                                        <table role="presentation" cellpadding="0" cellspacing="0" width="100%" style="margin-bottom: 16px;">
                                            <tr>
                                                <td style="
                                                    background: linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(99, 102, 241, 0.05) 100%);
                                                    border: 1px solid rgba(99, 102, 241, 0.2);
                                                    border-radius: 20px;
                                                    padding: 20px;
                                                ">
                                                    <table role="presentation" cellpadding="0" cellspacing="0" width="100%">
                                                        <tr>
                                                            <td width="60" style="vertical-align: top;">
                                                                <div style="
                                                                    width: 48px;
                                                                    height: 48px;
                                                                    background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
                                                                    border-radius: 14px;
                                                                    text-align: center;
                                                                    line-height: 48px;
                                                                    font-size: 24px;
                                                                    box-shadow: 0 8px 20px rgba(99, 102, 241, 0.3);
                                                                ">🔐</div>
                                                            </td>
                                                            <td style="vertical-align: top; padding-left: 16px;">
                                                                <h3 style="margin: 0 0 6px; font-size: 16px; font-weight: 700; color: #ffffff;">Enable 2FA Security</h3>
                                                                <p style="margin: 0; font-size: 14px; color: rgba(255, 255, 255, 0.5); line-height: 1.5;">Add an extra layer of protection to your account</p>
                                                            </td>
                                                            <td width="32" style="vertical-align: middle; text-align: right;">
                                                                <span style="font-size: 18px; color: rgba(255, 255, 255, 0.3);">→</span>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <!-- Feature Card 3 -->
                                        <table role="presentation" cellpadding="0" cellspacing="0" width="100%" style="margin-bottom: 32px;">
                                            <tr>
                                                <td style="
                                                    background: linear-gradient(135deg, rgba(236, 72, 153, 0.1) 0%, rgba(236, 72, 153, 0.05) 100%);
                                                    border: 1px solid rgba(236, 72, 153, 0.2);
                                                    border-radius: 20px;
                                                    padding: 20px;
                                                ">
                                                    <table role="presentation" cellpadding="0" cellspacing="0" width="100%">
                                                        <tr>
                                                            <td width="60" style="vertical-align: top;">
                                                                <div style="
                                                                    width: 48px;
                                                                    height: 48px;
                                                                    background: linear-gradient(135deg, #ec4899 0%, #f472b6 100%);
                                                                    border-radius: 14px;
                                                                    text-align: center;
                                                                    line-height: 48px;
                                                                    font-size: 24px;
                                                                    box-shadow: 0 8px 20px rgba(236, 72, 153, 0.3);
                                                                ">🎨</div>
                                                            </td>
                                                            <td style="vertical-align: top; padding-left: 16px;">
                                                                <h3 style="margin: 0 0 6px; font-size: 16px; font-weight: 700; color: #ffffff;">Explore Features</h3>
                                                                <p style="margin: 0; font-size: 14px; color: rgba(255, 255, 255, 0.5); line-height: 1.5;">Discover powerful tools to enhance your workflow</p>
                                                            </td>
                                                            <td width="32" style="vertical-align: middle; text-align: right;">
                                                                <span style="font-size: 18px; color: rgba(255, 255, 255, 0.3);">→</span>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <!-- CTA Button -->
                                        <table role="presentation" cellpadding="0" cellspacing="0" width="100%">
                                            <tr>
                                                <td align="center">
                                                    <a href="$websiteUrl" style="
                                                        display: inline-block;
                                                        background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #ec4899 100%);
                                                        color: #ffffff;
                                                        text-decoration: none;
                                                        font-size: 16px;
                                                        font-weight: 700;
                                                        padding: 18px 48px;
                                                        border-radius: 16px;
                                                        box-shadow: 
                                                            0 0 40px rgba(99, 102, 241, 0.4),
                                                            0 8px 24px rgba(0, 0, 0, 0.2);
                                                        letter-spacing: -0.3px;
                                                    ">
                                                        Start Exploring →
                                                    </a>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                    </td>
                                </tr>
                                
                                <!-- Stats/Social Proof Section -->
                                <tr>
                                    <td style="padding: 0 40px 40px;">
                                        <table role="presentation" cellpadding="0" cellspacing="0" width="100%" style="
                                            background: rgba(255, 255, 255, 0.03);
                                            border: 1px solid rgba(255, 255, 255, 0.06);
                                            border-radius: 20px;
                                            overflow: hidden;
                                        ">
                                            <tr>
                                                <td align="center" style="padding: 24px; border-right: 1px solid rgba(255, 255, 255, 0.06);">
                                                    <div style="font-size: 28px; font-weight: 800; color: #ffffff; margin-bottom: 4px;">10K+</div>
                                                    <div style="font-size: 12px; color: rgba(255, 255, 255, 0.4); text-transform: uppercase; letter-spacing: 0.5px;">Users</div>
                                                </td>
                                                <td align="center" style="padding: 24px; border-right: 1px solid rgba(255, 255, 255, 0.06);">
                                                    <div style="font-size: 28px; font-weight: 800; color: #ffffff; margin-bottom: 4px;">99%</div>
                                                    <div style="font-size: 12px; color: rgba(255, 255, 255, 0.4); text-transform: uppercase; letter-spacing: 0.5px;">Uptime</div>
                                                </td>
                                                <td align="center" style="padding: 24px;">
                                                    <div style="font-size: 28px; font-weight: 800; color: #ffffff; margin-bottom: 4px;">24/7</div>
                                                    <div style="font-size: 12px; color: rgba(255, 255, 255, 0.4); text-transform: uppercase; letter-spacing: 0.5px;">Support</div>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="
                                        padding: 32px 40px;
                                        border-top: 1px solid rgba(255, 255, 255, 0.06);
                                        text-align: center;
                                    ">
                                        <!-- Social Links -->
                                        <table role="presentation" cellpadding="0" cellspacing="0" style="margin: 0 auto 24px;">
                                            <tr>
                                                <td style="padding: 0 8px;">
                                                    <a href="#" style="
                                                        display: inline-block;
                                                        width: 36px;
                                                        height: 36px;
                                                        background: rgba(255, 255, 255, 0.05);
                                                        border: 1px solid rgba(255, 255, 255, 0.1);
                                                        border-radius: 10px;
                                                        text-align: center;
                                                        line-height: 36px;
                                                        font-size: 16px;
                                                        text-decoration: none;
                                                    ">🐦</a>
                                                </td>
                                                <td style="padding: 0 8px;">
                                                    <a href="#" style="
                                                        display: inline-block;
                                                        width: 36px;
                                                        height: 36px;
                                                        background: rgba(255, 255, 255, 0.05);
                                                        border: 1px solid rgba(255, 255, 255, 0.1);
                                                        border-radius: 10px;
                                                        text-align: center;
                                                        line-height: 36px;
                                                        font-size: 16px;
                                                        text-decoration: none;
                                                    ">📸</a>
                                                </td>
                                                <td style="padding: 0 8px;">
                                                    <a href="#" style="
                                                        display: inline-block;
                                                        width: 36px;
                                                        height: 36px;
                                                        background: rgba(255, 255, 255, 0.05);
                                                        border: 1px solid rgba(255, 255, 255, 0.1);
                                                        border-radius: 10px;
                                                        text-align: center;
                                                        line-height: 36px;
                                                        font-size: 16px;
                                                        text-decoration: none;
                                                    ">💼</a>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <p style="margin: 0 0 8px; font-size: 13px; color: rgba(255, 255, 255, 0.4);">
                                            Questions? We're here to help 24/7
                                        </p>
                                        <p style="margin: 0; font-size: 11px; color: rgba(255, 255, 255, 0.2);">
                                            © ${java.time.Year.now().value} $fromName. All rights reserved.
                                        </p>
                                    </td>
                                </tr>
                                
                            </table>
                        </td>
                    </tr>
                    
                </table>
                
            </td>
        </tr>
    </table>
    
</body>
</html>
        """.trimIndent()
    }

    private fun sendHtmlEmail(to: String, subject: String, htmlContent: String): Boolean {
        return try {
            val email = HtmlEmail().apply {
                hostName = host
                setSmtpPort(port)
                setAuthentication(username, password)
                isStartTLSEnabled = true
                setFrom(fromEmail, fromName)
                this.subject = subject
                setHtmlMsg(htmlContent)
                setTextMsg("Your verification code is in the HTML version of this email.")
                addTo(to)
            }
            email.send()
            logger.info("HTML email sent to $to: $subject")
            true
        } catch (e: Exception) {
            logger.error("Failed to send HTML email to $to: ${e.message}", e)
            false
        }
    }
}