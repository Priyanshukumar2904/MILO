#!/usr/bin/env python3
"""
MILO Android App Video Demonstration Generator
Generates an ultra-crisp 1080p demonstration video of the MILO Native Android App.
"""

import math
import os
import subprocess
import sys
from PIL import Image, ImageDraw, ImageFont

WIDTH = 1920
HEIGHT = 1080
FPS = 30
TOTAL_SECONDS = 56
TOTAL_FRAMES = FPS * TOTAL_SECONDS

# Color Palette (Black + White + Grayscale minimal, OLED)
BG_STUDIO = (12, 12, 14)
PHONE_BORDER = (45, 45, 52)
PHONE_BG = (16, 16, 18)
TEXT_WHITE = (252, 252, 254)
TEXT_MUTED = (165, 165, 175)
TEXT_DARK = (105, 105, 115)
CARD_BG = (24, 24, 28)
CARD_BORDER = (40, 40, 48)
ACCENT_WHITE = (255, 255, 255)
ACCENT_GREEN = (52, 199, 89)
ACCENT_AMBER = (255, 179, 64)
DIVIDER = (32, 32, 38)

FONT_BOLD_PATH = "/usr/share/fonts/TTF/DejaVuSans-Bold.ttf"
FONT_REG_PATH = "/usr/share/fonts/TTF/DejaVuSans.ttf"

def get_font(path, size):
    try:
        return ImageFont.truetype(path, size)
    except Exception:
        return ImageFont.load_default()

f_hero = get_font(FONT_BOLD_PATH, 42)
f_subhero = get_font(FONT_REG_PATH, 18)
f_h1 = get_font(FONT_BOLD_PATH, 28)
f_h2 = get_font(FONT_BOLD_PATH, 22)
f_h3 = get_font(FONT_BOLD_PATH, 16)
f_body = get_font(FONT_REG_PATH, 14)
f_body_bold = get_font(FONT_BOLD_PATH, 14)
f_small = get_font(FONT_REG_PATH, 12)
f_tiny = get_font(FONT_REG_PATH, 10)
f_score = get_font(FONT_BOLD_PATH, 54)
f_timer = get_font(FONT_BOLD_PATH, 64)

# Device Frame Dimensions
PW = 460
PH = 960
PX = 1320
PY = 60

def draw_device_mockup(draw, px, py, pw, ph):
    """Draws smartphone bezel."""
    draw.rounded_rectangle([px - 14, py - 14, px + pw + 14, py + ph + 14], radius=44, fill=(28, 28, 32), outline=(60, 60, 68), width=2)
    draw.rounded_rectangle([px - 4, py - 4, px + pw + 4, py + ph + 4], radius=38, fill=(18, 18, 20))
    draw.rounded_rectangle([px, py, px + pw, py + ph], radius=34, fill=PHONE_BG)
    
    # Status bar
    draw.text((px + 28, py + 14), "09:41", font=f_tiny, fill=TEXT_WHITE)
    draw.text((px + pw - 92, py + 14), "5G  100%", font=f_tiny, fill=TEXT_MUTED)
    draw.ellipse([px + pw // 2 - 6, py + 12, px + pw // 2 + 6, py + 24], fill=(10, 10, 12))
    # Bottom gesture bar
    draw.rounded_rectangle([px + pw // 2 - 45, py + ph - 12, px + pw // 2 + 45, py + ph - 8], radius=2, fill=(110, 110, 120))

def draw_milo_mascot(draw, cx, cy, emotion="Calm", anim_time=0.0, size=70):
    """Draws Milo the cat companion."""
    w = size
    h = int(size * 0.88)
    
    tail_swing = math.sin(anim_time * 5.0) * 10
    draw.line([cx - w//2 + 8, cy + h//2 - 6, cx - w//2 - 14 + int(tail_swing), cy + h//2 - 18], fill=TEXT_MUTED, width=4)
    draw.ellipse([cx - w//2, cy - h//2, cx + w//2, cy + h//2], fill=(235, 235, 240), outline=(255, 255, 255), width=2)
    
    ear_w = int(w * 0.28)
    ear_h = int(h * 0.42)
    twitch = math.sin(anim_time * 7.0) * (3 if emotion in ["Curious", "Happy"] else 0)
    
    # Left Ear
    draw.polygon([(cx - w//2 + 6, cy - h//2 + 10), (cx - w//2 + ear_w//2, cy - h//2 - ear_h + int(twitch)), (cx - w//2 + ear_w, cy - h//2 + 2)], fill=(235, 235, 240), outline=(255, 255, 255))
    draw.polygon([(cx - w//2 + 9, cy - h//2 + 8), (cx - w//2 + ear_w//2, cy - h//2 - ear_h + 6 + int(twitch)), (cx - w//2 + ear_w - 3, cy - h//2 + 4)], fill=(195, 195, 202))
    
    # Right Ear
    draw.polygon([(cx + w//2 - ear_w, cy - h//2 + 2), (cx + w//2 - ear_w//2, cy - h//2 - ear_h - int(twitch)), (cx + w//2 - 6, cy - h//2 + 10)], fill=(235, 235, 240), outline=(255, 255, 255))
    draw.polygon([(cx + w//2 - ear_w + 3, cy - h//2 + 4), (cx + w//2 - ear_w//2, cy - h//2 - ear_h + 6 - int(twitch)), (cx + w//2 - 9, cy - h//2 + 8)], fill=(195, 195, 202))
    
    # Eyes
    blink = (math.sin(anim_time * 2.5) > 0.94) and (emotion != "Sleepy")
    eye_offset = int(w * 0.22)
    eye_y = cy - int(h * 0.08)
    
    if emotion == "Sleepy" or blink or emotion in ["Happy", "Celebrating"]:
        draw.arc([cx - eye_offset - 8, eye_y - 6, cx - eye_offset + 8, eye_y + 8], start=200, end=340, fill=(30, 30, 35), width=3)
        draw.arc([cx + eye_offset - 8, eye_y - 6, cx + eye_offset + 8, eye_y + 8], start=200, end=340, fill=(30, 30, 35), width=3)
    else:
        draw.ellipse([cx - eye_offset - 6, eye_y - 6, cx - eye_offset + 6, eye_y + 6], fill=(20, 20, 25))
        draw.ellipse([cx + eye_offset - 6, eye_y - 6, cx + eye_offset + 6, eye_y + 6], fill=(20, 20, 25))
        draw.ellipse([cx - eye_offset - 4, eye_y - 4, cx - eye_offset, eye_y], fill=(255, 255, 255))
        draw.ellipse([cx + eye_offset - 4, eye_y - 4, cx + eye_offset, eye_y], fill=(255, 255, 255))
        
    draw.ellipse([cx - eye_offset - 8, eye_y + 7, cx - eye_offset - 2, eye_y + 13], fill=(215, 215, 220))
    draw.ellipse([cx + eye_offset + 2, eye_y + 7, cx + eye_offset + 8, eye_y + 13], fill=(215, 215, 220))
    
    draw.polygon([(cx - 3, cy + 3), (cx + 3, cy + 3), (cx, cy + 7)], fill=(60, 60, 65))
    draw.arc([cx - 8, cy + 4, cx, cy + 12], start=20, end=160, fill=(60, 60, 65), width=2)
    draw.arc([cx, cy + 4, cx + 8, cy + 12], start=20, end=160, fill=(60, 60, 65), width=2)
    
    draw.line([cx - w//2 + 4, cy + 2, cx - eye_offset - 10, cy], fill=(160, 160, 165), width=1)
    draw.line([cx - w//2 + 2, cy + 8, cx - eye_offset - 10, cy + 9], fill=(160, 160, 165), width=1)
    draw.line([cx + eye_offset + 10, cy, cx + w//2 - 4, cy + 2], fill=(160, 160, 165), width=1)
    draw.line([cx + eye_offset + 10, cy + 9, cx + w//2 - 2, cy + 8], fill=(160, 160, 165), width=1)

    paw_y = cy + h//2 - 4
    if emotion in ["Celebrating", "Welcoming"]:
        paw_wave = int(math.sin(anim_time * 8.0) * 5)
        draw.ellipse([cx + w//2 - 12, cy - 8 + paw_wave, cx + w//2 + 4, cy + 10 + paw_wave], fill=(245, 245, 250), outline=(255, 255, 255))
        draw.ellipse([cx - w//3 - 7, paw_y - 6, cx - w//3 + 7, paw_y + 6], fill=(245, 245, 250), outline=(255, 255, 255))
    else:
        draw.ellipse([cx - w//4 - 7, paw_y - 6, cx - w//4 + 7, paw_y + 6], fill=(245, 245, 250), outline=(255, 255, 255))
        draw.ellipse([cx + w//4 - 7, paw_y - 6, cx + w//4 + 7, paw_y + 6], fill=(245, 245, 250), outline=(255, 255, 255))

def draw_speech_bubble(draw, x, y, text, max_w=340):
    """Draws rounded speech bubble with pointer tail."""
    tw = min(max_w, len(text) * 7 + 28)
    th = 38
    draw.rounded_rectangle([x, y, x + tw, y + th], radius=10, fill=(32, 32, 38), outline=(50, 50, 60))
    # Little pointer
    draw.polygon([(x - 8, y + 18), (x, y + 12), (x, y + 24)], fill=(32, 32, 38))
    draw.text((x + 12, y + 11), text, font=f_small, fill=TEXT_WHITE)

def draw_bottom_nav(draw, px, py, pw, ph, active_tab="Today"):
    bar_h = 60
    by = py + ph - bar_h - 16
    draw.rectangle([px, by, px + pw, py + ph - 14], fill=(18, 18, 22))
    draw.line([px, by, px + pw, by], fill=DIVIDER, width=1)
    
    tabs = ["Today", "Schedule", "Habits", "Insights", "Profile"]
    tab_w = pw // len(tabs)
    for i, t in enumerate(tabs):
        tx = px + i * tab_w + tab_w // 2
        is_active = (t == active_tab)
        color = TEXT_WHITE if is_active else TEXT_DARK
        if is_active:
            draw.ellipse([tx - 10, by + 10, tx + 10, by + 30], fill=(36, 36, 42))
        draw.text((tx - len(t)*3 - 3, by + 36), t, font=f_tiny, fill=color)

def draw_left_sidebar(draw, chapter_idx, chapter_title, subtext, bullets):
    # App Header
    draw.text((90, 70), "MILO", font=f_hero, fill=TEXT_WHITE)
    draw.text((90, 126), "Personal Productivity OS  •  Kotlin & Jetpack Compose", font=f_subhero, fill=TEXT_MUTED)
    draw.text((90, 156), "“Be better than yesterday.”", font=f_body_bold, fill=ACCENT_WHITE)
    draw.line([90, 195, 450, 195], fill=(50, 50, 56), width=2)
    
    # Section
    draw.text((90, 230), chapter_idx, font=f_tiny, fill=TEXT_MUTED)
    draw.text((90, 255), chapter_title, font=f_h1, fill=TEXT_WHITE)
    draw.text((90, 300), subtext, font=f_body, fill=TEXT_MUTED)
    
    # Callout Cards
    cy = 360
    for bullet in bullets:
        draw.rounded_rectangle([90, cy, 1220, cy + 68], radius=12, fill=CARD_BG, outline=CARD_BORDER, width=1)
        draw.ellipse([110, cy + 26, 124, cy + 40], fill=TEXT_WHITE)
        draw.text((140, cy + 14), bullet[0], font=f_body_bold, fill=TEXT_WHITE)
        draw.text((140, cy + 38), bullet[1], font=f_small, fill=TEXT_MUTED)
        cy += 82
        
    # Technical Specs Badge Bar
    ay = 930
    draw.text((90, ay), "NATIVE ANDROID ARCHITECTURE", font=f_tiny, fill=TEXT_DARK)
    techs = ["Kotlin 2.0", "Jetpack Compose", "Room SQLite", "WorkManager Sync", "SHA-256 Verified", "100% Offline"]
    tx = 90
    for tech in techs:
        tw = len(tech) * 8 + 18
        draw.rounded_rectangle([tx, ay + 20, tx + tw, ay + 48], radius=6, fill=(24, 24, 28), outline=(42, 42, 48))
        draw.text((tx + 9, ay + 27), tech, font=f_tiny, fill=TEXT_MUTED)
        tx += tw + 10

def render_frame(frame_idx):
    img = Image.new("RGB", (WIDTH, HEIGHT), BG_STUDIO)
    draw = ImageDraw.Draw(img)
    
    # Ambient grid dots
    for gx in range(0, WIDTH, 60):
        for gy in range(0, HEIGHT, 60):
            draw.point((gx, gy), fill=(24, 24, 28))
            
    t = frame_idx / FPS
    draw_device_mockup(draw, PX, PY, PW, PH)
    
    # ----------------------------------------------------
    # SCENE 1: Intro & Milo Mascot Companion (0s - 8s)
    # ----------------------------------------------------
    if t < 8.0:
        local_t = t
        emotions = ["Welcoming", "Curious", "Happy", "Proud"]
        emotion = emotions[min(3, int(local_t / 2.0))]
        
        draw_left_sidebar(
            draw,
            "CHAPTER 01 / 07",
            "Milo: The Emotional Companion",
            "A supportive graphical mascot with 8 distinct emotional states, offering positive reinforcement without guilt.",
            [
                ("8 Canvas-Driven Emotional States", "Calm, Curious, Happy, Proud, Encouraging, Sleepy, Celebrating, Welcoming."),
                ("Emotionally Safe Feedback", "No streak-shaming, guilt trips, or condescending copy on difficult days."),
                ("Dynamic Vector Animations", "Real-time canvas rendering with ear twitches, eye blinks, and interactive reactions.")
            ]
        )
        
        # Phone Content
        # App Bar
        draw.text((PX + 24, PY + 52), "MILO", font=f_h2, fill=TEXT_WHITE)
        draw.text((PX + 24, PY + 80), "Personal Productivity OS", font=f_tiny, fill=TEXT_MUTED)
        draw.rounded_rectangle([PX + PW - 100, PY + 56, PX + PW - 24, PY + 80], radius=12, fill=(28, 28, 34), outline=CARD_BORDER)
        draw.text((PX + PW - 88, PY + 62), "Offline Sync ✓", font=f_tiny, fill=ACCENT_GREEN)
        
        # Big Center Milo Demonstration
        draw.rounded_rectangle([PX + 20, PY + 110, PX + PW - 20, PY + 420], radius=20, fill=CARD_BG, outline=CARD_BORDER)
        draw_milo_mascot(draw, PX + PW//2, PY + 240, emotion=emotion, anim_time=t, size=110)
        
        # Dynamic speech
        bubbles = {
            "Welcoming": "Welcome back, Priyanshu! Let's build momentum.",
            "Curious": "What's the #1 priority we're tackling next?",
            "Happy": "4 deep focus blocks scheduled today. Looking solid!",
            "Proud": "You're consistently showing up. That's what counts."
        }
        speech = bubbles.get(emotion, "Let's make today count.")
        draw_speech_bubble(draw, PX + 50, PY + 330, speech, max_w=360)
        
        # Emotion selector strip
        draw.text((PX + 24, PY + 450), "ACTIVE COMPANION STATE", font=f_tiny, fill=TEXT_MUTED)
        ex = PX + 24
        for em in ["Welcoming", "Curious", "Happy", "Proud"]:
            is_active = (em == emotion)
            fill_col = TEXT_WHITE if is_active else CARD_BG
            text_col = (10, 10, 12) if is_active else TEXT_MUTED
            draw.rounded_rectangle([ex, PY + 472, ex + 94, PY + 506], radius=8, fill=fill_col, outline=CARD_BORDER)
            draw.text((ex + 12, PY + 482), em, font=f_tiny, fill=text_col)
            ex += 104
            
        # Feature highlight card below
        draw.rounded_rectangle([PX + 20, PY + 530, PX + PW - 20, PY + 680], radius=16, fill=CARD_BG, outline=CARD_BORDER)
        draw.text((PX + 36, PY + 550), "Today's Philosophy", font=f_h3, fill=TEXT_WHITE)
        draw.text((PX + 36, PY + 580), "“You don't need a perfect day.", font=f_body, fill=TEXT_MUTED)
        draw.text((PX + 36, PY + 602), "You just need to be a little better", font=f_body, fill=TEXT_MUTED)
        draw.text((PX + 36, PY + 624), "than yesterday.”", font=f_body_bold, fill=ACCENT_WHITE)
        
        draw_bottom_nav(draw, PX, PY, PW, PH, "Today")

    # ----------------------------------------------------
    # SCENE 2: Today Dashboard & Scoring Engine (8s - 17s)
    # ----------------------------------------------------
    elif t < 17.0:
        local_t = t - 8.0
        # Animate score ring progress
        progress_factor = min(1.0, local_t / 3.0)
        current_score = int(78 * progress_factor)
        
        draw_left_sidebar(
            draw,
            "CHAPTER 02 / 07",
            "Today OS & 0-100 Scoring",
            "An intelligent personal dashboard that quantifies daily momentum with weighted inputs and transparent feedback.",
            [
                ("Modular Productivity Score (0-100)", "Weighted: 25% Completion, 20% Time, 20% Deep Focus, 20% Habits, 15% Routine."),
                ("Real-Time Improvement Delta (+9%)", "Direct comparison against yesterday's baseline provides continuous reinforcement."),
                ("Vertical Timeline with Live Tracking", "Track current active blocks seamlessly with native Android Room persistence.")
            ]
        )
        
        # Phone Screen
        draw.text((PX + 24, PY + 48), "TODAY", font=f_h1, fill=TEXT_WHITE)
        draw.text((PX + 24, PY + 80), "Saturday, Sep 5  •  Day 24 Streak", font=f_tiny, fill=TEXT_MUTED)
        
        # Mini mascot at top right
        draw_milo_mascot(draw, PX + PW - 55, PY + 70, emotion="Happy", anim_time=t, size=46)
        
        # Score Ring Container
        draw.rounded_rectangle([PX + 20, PY + 105, PX + PW - 20, PY + 340], radius=18, fill=CARD_BG, outline=CARD_BORDER)
        
        # Animated Ring
        rcx = PX + PW // 2
        rcy = PY + 200
        radius = 70
        # Background arc
        draw.arc([rcx - radius, rcy - radius, rcx + radius, rcy + radius], start=0, end=360, fill=(45, 45, 52), width=10)
        # Active arc
        if progress_factor > 0:
            sweep = int(360 * (current_score / 100.0))
            draw.arc([rcx - radius, rcy - radius, rcx + radius, rcy + radius], start=-90, end=-90 + sweep, fill=ACCENT_WHITE, width=10)
            
        # Inner text
        draw.text((rcx - 34, rcy - 34), str(current_score), font=f_score, fill=TEXT_WHITE)
        draw.text((rcx - 18, rcy + 22), "/ 100", font=f_tiny, fill=TEXT_MUTED)
        
        # Delta badge
        draw.rounded_rectangle([rcx - 48, PY + 295, rcx + 48, PY + 322], radius=12, fill=(30, 48, 36), outline=(42, 80, 52))
        draw.text((rcx - 38, PY + 301), "▲ +9% vs yest", font=f_tiny, fill=ACCENT_GREEN)
        
        # Metric Pills
        metrics = [("Tasks", "82%"), ("Focus", "88%"), ("Habits", "84%"), ("Routine", "80%")]
        mx = PX + 20
        for name, val in metrics:
            draw.rounded_rectangle([mx, PY + 355, mx + 98, PY + 400], radius=10, fill=CARD_BG, outline=CARD_BORDER)
            draw.text((mx + 10, PY + 363), name, font=f_tiny, fill=TEXT_MUTED)
            draw.text((mx + 10, PY + 378), val, font=f_h3, fill=TEXT_WHITE)
            mx += 108
            
        # Timeline Title
        draw.text((PX + 24, PY + 420), "DAILY TIMELINE", font=f_tiny, fill=TEXT_MUTED)
        
        # Timeline Nodes
        # Node 1: Completed
        draw.rounded_rectangle([PX + 20, PY + 440, PX + PW - 20, PY + 505], radius=12, fill=CARD_BG, outline=CARD_BORDER)
        draw.ellipse([PX + 34, PY + 458, PX + 52, PY + 476], fill=ACCENT_GREEN)
        draw.text((PX + 38, PY + 460), "✓", font=f_tiny, fill=(10, 10, 12))
        draw.text((PX + 68, PY + 450), "Morning Deep Work", font=f_body_bold, fill=TEXT_WHITE)
        draw.text((PX + 68, PY + 474), "08:00 – 10:00  •  Deep Focus  •  120m", font=f_tiny, fill=TEXT_MUTED)
        
        # Node 2: Live Active Tracking Node
        pulse = (math.sin(t * 8.0) + 1.0) / 2.0
        active_border = (int(100 + pulse * 155), int(100 + pulse * 155), int(100 + pulse * 155))
        draw.rounded_rectangle([PX + 20, PY + 520, PX + PW - 20, PY + 600], radius=12, fill=(28, 28, 36), outline=active_border, width=2)
        draw.ellipse([PX + 36, PY + 544, PX + 50, PY + 558], fill=ACCENT_WHITE)
        draw.text((PX + 68, PY + 532), "Core Architecture Sync", font=f_body_bold, fill=TEXT_WHITE)
        draw.text((PX + 68, PY + 554), "10:30 – 12:00  •  LIVE TRACKING", font=f_tiny, fill=ACCENT_GREEN)
        draw.text((PX + 68, PY + 574), "Elapsed: 42m / 90m planned", font=f_tiny, fill=TEXT_MUTED)
        
        # Node 3: Upcoming
        draw.rounded_rectangle([PX + 20, PY + 615, PX + PW - 20, PY + 680], radius=12, fill=CARD_BG, outline=CARD_BORDER)
        draw.ellipse([PX + 36, PY + 636, PX + 48, PY + 648], outline=TEXT_MUTED, width=2)
        draw.text((PX + 68, PY + 626), "Algorithm Practice & Review", font=f_body_bold, fill=TEXT_MUTED)
        draw.text((PX + 68, PY + 650), "14:00 – 15:30  •  Study Block", font=f_tiny, fill=TEXT_DARK)
        
        # Habit strip quick toggle
        draw.text((PX + 24, PY + 700), "HABITS TODAY (2 / 3 COMPLETED)", font=f_tiny, fill=TEXT_MUTED)
        draw.rounded_rectangle([PX + 20, PY + 720, PX + PW - 20, PY + 775], radius=12, fill=CARD_BG, outline=CARD_BORDER)
        draw.text((PX + 36, PY + 735), "Morning Run  ✓", font=f_small, fill=ACCENT_GREEN)
        draw.text((PX + 170, PY + 735), "Hydration  ✓", font=f_small, fill=ACCENT_GREEN)
        draw.text((PX + 295, PY + 735), "Reading  ○", font=f_small, fill=TEXT_MUTED)
        
        draw_bottom_nav(draw, PX, PY, PW, PH, "Today")

    # ----------------------------------------------------
    # SCENE 3: Live Focus Timer & Zen Screen (17s - 25s)
    # ----------------------------------------------------
    elif t < 25.0:
        local_t = t - 17.0
        sec_left = max(0, 1500 - int(local_t * 12))
        mins = sec_left // 60
        secs = sec_left % 60
        timer_str = f"{mins:02d}:{secs:02d}"
        
        draw_left_sidebar(
            draw,
            "CHAPTER 03 / 07",
            "Zen Ambient Focus Engine",
            "An immersive, distraction-free environment that helps you sustain unbroken deep work sessions.",
            [
                ("Minimalist Ambient Display", "Black canvas with concentric breathing animations minimizes screen distractions."),
                ("Battery-Aware Persistence", "Native WorkManager integration ensures timers survive process kills and device reboots."),
                ("Milo Supportive Coaching", "Encouraging presence monitors your cadence without distracting interruptions.")
            ]
        )
        
        # Phone Screen: Zen Focus Mode
        draw.rounded_rectangle([PX, PY, PX + PW, PY + PH], radius=34, fill=(10, 10, 12))
        draw_device_mockup(draw, PX, PY, PW, PH)
        
        # Header in Zen Screen
        draw.text((PX + PW//2 - 40, PY + 60), "ZEN FOCUS", font=f_tiny, fill=TEXT_MUTED)
        draw.rounded_rectangle([PX + PW//2 - 90, PY + 95, PX + PW//2 + 90, PY + 125], radius=14, fill=CARD_BG, outline=CARD_BORDER)
        draw.text((PX + PW//2 - 76, PY + 103), "DEEP WORK • COMPILER", font=f_tiny, fill=TEXT_WHITE)
        
        # Concentric breathing circles
        pulse_r = int(math.sin(t * 3.0) * 12)
        cx = PX + PW // 2
        cy = PY + 340
        draw.ellipse([cx - 150 - pulse_r, cy - 150 - pulse_r, cx + 150 + pulse_r, cy + 150 + pulse_r], outline=(30, 30, 36), width=1)
        draw.ellipse([cx - 120, cy - 120, cx + 120, cy + 120], outline=(42, 42, 50), width=2)
        draw.ellipse([cx - 100, cy - 100, cx + 100, cy + 100], outline=ACCENT_WHITE, width=4)
        
        # Big Countdown Timer
        draw.text((cx - 105, cy - 36), timer_str, font=f_timer, fill=TEXT_WHITE)
        draw.text((cx - 52, cy + 42), "FOCUS BLOCK 2 OF 4", font=f_tiny, fill=TEXT_MUTED)
        
        # Mascot in encouraging mode
        draw_milo_mascot(draw, cx, PY + 550, emotion="Encouraging", anim_time=t, size=75)
        draw_speech_bubble(draw, cx - 140, PY + 610, "Stay with the code. You're in deep flow.", max_w=300)
        
        # Action Buttons
        draw.rounded_rectangle([PX + 40, PY + 710, PX + PW - 40, PY + 770], radius=16, fill=TEXT_WHITE)
        draw.text((PX + PW//2 - 50, PY + 730), "PAUSE SESSION", font=f_body_bold, fill=(10, 10, 12))
        
        draw.rounded_rectangle([PX + 40, PY + 790, PX + PW - 40, PY + 845], radius=16, fill=CARD_BG, outline=CARD_BORDER)
        draw.text((PX + PW//2 - 62, PY + 810), "+5 MIN EXTENSION", font=f_body, fill=TEXT_WHITE)

    # ----------------------------------------------------
    # SCENE 4: Multi-Scale Schedule Engine (25s - 34s)
    # ----------------------------------------------------
    elif t < 34.0:
        draw_left_sidebar(
            draw,
            "CHAPTER 04 / 07",
            "Multi-Scale Schedule Engine",
            "Time-blocking that adapts to your life with fluid Day, Week, and Month perspectives.",
            [
                ("Visual Time-Blocking", "Distinguish Deep Work, Routines, Health, and Recovery with high-contrast grayscale badges."),
                ("Gesture-Driven Switching", "Effortlessly toggle between Day granularity and long-range Month overviews."),
                ("Zero Cognitive Overhead", "Clear visual hierarchies prevent schedule paralysis and decision fatigue.")
            ]
        )
        
        # Phone Screen
        draw.text((PX + 24, PY + 48), "SCHEDULE", font=f_h1, fill=TEXT_WHITE)
        draw.text((PX + 24, PY + 80), "Planned vs Actual Cadence", font=f_tiny, fill=TEXT_MUTED)
        
        # Day / Week / Month Pill Selector
        draw.rounded_rectangle([PX + 20, PY + 105, PX + PW - 20, PY + 148], radius=12, fill=(22, 22, 26), outline=CARD_BORDER)
        # Active: Day
        draw.rounded_rectangle([PX + 24, PY + 109, PX + 150, PY + 144], radius=9, fill=TEXT_WHITE)
        draw.text((PX + 68, PY + 119), "Day", font=f_body_bold, fill=(10, 10, 12))
        draw.text((PX + 210, PY + 119), "Week", font=f_body, fill=TEXT_MUTED)
        draw.text((PX + 340, PY + 119), "Month", font=f_body, fill=TEXT_MUTED)
        
        # Time Blocks
        schedule_items = [
            ("08:00 – 10:00", "Deep Work: Native Engine", "DEEP WORK", ACCENT_GREEN, True),
            ("10:30 – 11:30", "Team Architecture Sync", "WORK", TEXT_MUTED, True),
            ("12:00 – 13:00", "Nutritious Lunch & Walk", "HEALTH", TEXT_MUTED, True),
            ("13:30 – 15:30", "Video Demo Pipeline", "DEEP WORK", ACCENT_WHITE, False),
            ("16:00 – 17:00", "Gym & Cardio Session", "EXERCISE", TEXT_MUTED, False),
            ("19:00 – 20:00", "Reading & Journaling", "ROUTINE", TEXT_MUTED, False)
        ]
        
        sy = PY + 168
        for time_str, title, cat, color, done in schedule_items:
            draw.rounded_rectangle([PX + 20, sy, PX + PW - 20, sy + 70], radius=12, fill=CARD_BG, outline=CARD_BORDER)
            # Tag pill
            draw.rounded_rectangle([PX + 34, sy + 12, PX + 130, sy + 32], radius=6, fill=(34, 34, 40))
            draw.text((PX + 42, sy + 16), cat, font=f_tiny, fill=TEXT_WHITE)
            draw.text((PX + 142, sy + 16), time_str, font=f_tiny, fill=TEXT_MUTED)
            draw.text((PX + 34, sy + 40), title, font=f_body_bold, fill=TEXT_WHITE if not done else TEXT_MUTED)
            if done:
                draw.text((PX + PW - 55, sy + 24), "✓", font=f_h2, fill=ACCENT_GREEN)
            else:
                draw.text((PX + PW - 55, sy + 24), "○", font=f_h2, fill=TEXT_MUTED)
            sy += 78
            
        draw_bottom_nav(draw, PX, PY, PW, PH, "Schedule")

    # ----------------------------------------------------
    # SCENE 5: Habit Consistency Matrix (34s - 43s)
    # ----------------------------------------------------
    elif t < 43.0:
        draw_left_sidebar(
            draw,
            "CHAPTER 05 / 07",
            "Habit Consistency Matrix",
            "A visual 7-day completion grid and velocity tracker built on positive reinforcement.",
            [
                ("7-Day Adherence Matrix", "Every habit displays visual check matrices across the trailing 7 days."),
                ("Streak Resilience Without Guilt", "Designed to celebrate consistency velocity rather than punishing unavoidable missed days."),
                ("Sample Size Citing", "“Based on the last 4 weeks, your consistency velocity is up +15.8%.”")
            ]
        )
        
        # Phone Screen
        draw.text((PX + 24, PY + 48), "HABIT ENGINE", font=f_h1, fill=TEXT_WHITE)
        draw.text((PX + 24, PY + 80), "Consistency Over Perfection", font=f_tiny, fill=TEXT_MUTED)
        
        # Habit 1: Morning Run
        hy = PY + 115
        draw.rounded_rectangle([PX + 20, hy, PX + PW - 20, hy + 135], radius=16, fill=CARD_BG, outline=CARD_BORDER)
        draw.text((PX + 36, hy + 16), "Morning Run & Cardio", font=f_body_bold, fill=TEXT_WHITE)
        draw.text((PX + 36, hy + 40), "12 Day Streak  •  94% Consistency", font=f_tiny, fill=ACCENT_GREEN)
        
        # 7-day matrix squares
        days = ["M", "T", "W", "T", "F", "S", "S"]
        checks = [True, True, True, True, True, True, True]
        mx = PX + 36
        for i in range(7):
            draw.text((mx + 10, hy + 66), days[i], font=f_tiny, fill=TEXT_MUTED)
            draw.rounded_rectangle([mx, hy + 86, mx + 38, hy + 120], radius=6, fill=TEXT_WHITE if checks[i] else (35, 35, 42))
            if checks[i]:
                draw.text((mx + 12, hy + 94), "✓", font=f_tiny, fill=(10, 10, 12))
            mx += 56
            
        # Habit 2: Deep Reading
        hy = PY + 265
        draw.rounded_rectangle([PX + 20, hy, PX + PW - 20, hy + 135], radius=16, fill=CARD_BG, outline=CARD_BORDER)
        draw.text((PX + 36, hy + 16), "Deep Reading (20m+)", font=f_body_bold, fill=TEXT_WHITE)
        draw.text((PX + 36, hy + 40), "7 Day Streak  •  88% Consistency", font=f_tiny, fill=TEXT_MUTED)
        checks2 = [True, True, True, False, True, True, True]
        mx = PX + 36
        for i in range(7):
            draw.text((mx + 10, hy + 66), days[i], font=f_tiny, fill=TEXT_MUTED)
            draw.rounded_rectangle([mx, hy + 86, mx + 38, hy + 120], radius=6, fill=TEXT_WHITE if checks2[i] else (35, 35, 42))
            if checks2[i]:
                draw.text((mx + 12, hy + 94), "✓", font=f_tiny, fill=(10, 10, 12))
            mx += 56
            
        # Behavioral Insight Card
        draw.rounded_rectangle([PX + 20, PY + 420, PX + PW - 20, PY + 550], radius=16, fill=(28, 28, 36), outline=(48, 48, 60))
        draw.text((PX + 36, PY + 438), "BEHAVIORAL INSIGHT", font=f_tiny, fill=ACCENT_AMBER)
        draw.text((PX + 36, PY + 462), "“Based on the last 4 weeks,", font=f_body, fill=TEXT_WHITE)
        draw.text((PX + 36, PY + 484), "your habit streak velocity is up +15.8%.", font=f_body_bold, fill=ACCENT_GREEN)
        draw.text((PX + 36, PY + 510), "Sample: 28 days tracked • 84% adherence", font=f_tiny, fill=TEXT_MUTED)
        
        # Mascot in Proud state
        draw_milo_mascot(draw, PX + PW//2, PY + 640, emotion="Proud", anim_time=t, size=85)
        draw_speech_bubble(draw, PX + 60, PY + 720, "12 days straight on fitness! That is discipline.", max_w=340)
        
        draw_bottom_nav(draw, PX, PY, PW, PH, "Habits")

    # ----------------------------------------------------
    # SCENE 6: Reports & Monthly Life Report (43s - 51s)
    # ----------------------------------------------------
    elif t < 51.0:
        draw_left_sidebar(
            draw,
            "CHAPTER 06 / 07",
            "The Monthly Life Report",
            "Transforms raw time data into an inspiring narrative of personal transformation.",
            [
                ("“You Showed Up.”", "The signature Milo monthly statement acknowledging genuine effort and commitment."),
                ("Deep Time Breakdown", "Track total productive hours, deep study blocks, and exercise frequency at a glance."),
                ("Personal Records & Peaks", "Discover your peak biological productivity windows (9 AM – 12 PM).")
            ]
        )
        
        # Phone Screen: Monthly Life Report
        draw.text((PX + 24, PY + 48), "LIFE REPORT", font=f_h1, fill=TEXT_WHITE)
        draw.text((PX + 24, PY + 80), "September 2026 Monthly Summary", font=f_tiny, fill=TEXT_MUTED)
        
        # Signature Headline Box
        draw.rounded_rectangle([PX + 20, PY + 110, PX + PW - 20, PY + 240], radius=18, fill=CARD_BG, outline=CARD_BORDER)
        draw.text((PX + 36, PY + 130), "“You showed up.”", font=f_h1, fill=ACCENT_WHITE)
        draw.text((PX + 36, PY + 170), "You completed 86% of your planned", font=f_body, fill=TEXT_MUTED)
        draw.text((PX + 36, PY + 192), "activities. You are not the same person", font=f_body, fill=TEXT_MUTED)
        draw.text((PX + 36, PY + 214), "who started this month.", font=f_body_bold, fill=TEXT_WHITE)
        
        # Big Stats Grid
        stats = [
            ("24", "Days Active", ACCENT_GREEN),
            ("125h", "Total Productive", TEXT_WHITE),
            ("37h", "Deep Study", TEXT_WHITE),
            ("14", "Workouts", ACCENT_GREEN)
        ]
        
        grid_positions = [
            (PX + 20, PY + 255), (PX + PW//2 + 5, PY + 255),
            (PX + 20, PY + 345), (PX + PW//2 + 5, PY + 345)
        ]
        
        for (val, label, col), (gx, gy) in zip(stats, grid_positions):
            draw.rounded_rectangle([gx, gy, gx + PW//2 - 25, gy + 78], radius=14, fill=CARD_BG, outline=CARD_BORDER)
            draw.text((gx + 16, gy + 12), val, font=f_h1, fill=col)
            draw.text((gx + 16, gy + 46), label, font=f_tiny, fill=TEXT_MUTED)
            
        # Celebrating Mascot with festive sparkle dots
        mcx = PX + PW // 2
        mcy = PY + 520
        # Sparkles
        for i in range(12):
            angle = (t * 4.0 + i * (math.pi / 6))
            sp_x = mcx + int(math.cos(angle) * (80 + i * 4))
            sp_y = mcy + int(math.sin(angle) * (55 + i * 3))
            draw.point((sp_x, sp_y), fill=ACCENT_WHITE)
            draw.point((sp_x + 1, sp_y), fill=ACCENT_WHITE)
            
        draw_milo_mascot(draw, mcx, mcy, emotion="Celebrating", anim_time=t, size=95)
        draw_speech_bubble(draw, PX + 40, PY + 615, "Look at what you achieved this month!", max_w=380)
        
        # Peak Window Card
        draw.rounded_rectangle([PX + 20, PY + 680, PX + PW - 20, PY + 760], radius=14, fill=CARD_BG, outline=CARD_BORDER)
        draw.text((PX + 36, PY + 695), "Peak Focus Window", font=f_tiny, fill=TEXT_MUTED)
        draw.text((PX + 36, PY + 718), "9:00 AM – 12:00 PM (Score: 92/100)", font=f_body_bold, fill=TEXT_WHITE)
        
        draw_bottom_nav(draw, PX, PY, PW, PH, "Insights")

    # ----------------------------------------------------
    # SCENE 7: Verified Distribution & Outro (51s - 56s)
    # ----------------------------------------------------
    else:
        draw_left_sidebar(
            draw,
            "CHAPTER 07 / 07",
            "Self-Hosted Distribution Pipeline",
            "Autonomous in-app update delivery with cryptographically verified package integrity.",
            [
                ("SHA-256 Checksum Verification", "Every APK download is hashed locally before invocation of the Android Package Installer."),
                ("Zero External Store Dependency", "Direct delivery from GitHub Releases ensures complete user autonomy."),
                ("Production-Grade Android Architecture", "Pure Kotlin 2.0, Jetpack Compose, Room SQLite, WorkManager, and ProGuard minification.")
            ]
        )
        
        # Phone Screen: Update Verification Dialog
        draw.rounded_rectangle([PX + 20, PY + 180, PX + PW - 20, PY + 540], radius=20, fill=(24, 24, 30), outline=(60, 60, 72), width=2)
        
        draw.text((PX + 40, PY + 210), "Update Available", font=f_h2, fill=TEXT_WHITE)
        draw.text((PX + 40, PY + 242), "MILO v1.4.0 (Build 14)", font=f_body_bold, fill=ACCENT_GREEN)
        
        # Integrity box
        draw.rounded_rectangle([PX + 36, PY + 280, PX + PW - 36, PY + 360], radius=12, fill=(16, 16, 20), outline=CARD_BORDER)
        draw.text((PX + 48, PY + 292), "INTEGRITY CHECKSUM VERIFIED ✓", font=f_tiny, fill=ACCENT_GREEN)
        draw.text((PX + 48, PY + 312), "SHA-256: e3b0c44298fc1c149afbf4c8...", font=f_tiny, fill=TEXT_MUTED)
        draw.text((PX + 48, PY + 332), "Size: 18.4 MB  •  Signed Release", font=f_tiny, fill=TEXT_DARK)
        
        # What's new bullets
        draw.text((PX + 40, PY + 380), "What's New in v1.4.0:", font=f_tiny, fill=TEXT_MUTED)
        draw.text((PX + 40, PY + 405), "• 8 expressive mascot emotional states", font=f_tiny, fill=TEXT_WHITE)
        draw.text((PX + 40, PY + 425), "• 0-100 Personal Productivity Scoring", font=f_tiny, fill=TEXT_WHITE)
        draw.text((PX + 40, PY + 445), "• 7-day habit consistency matrices", font=f_tiny, fill=TEXT_WHITE)
        
        # Install Button
        draw.rounded_rectangle([PX + 36, PY + 475, PX + PW - 36, PY + 520], radius=12, fill=TEXT_WHITE)
        draw.text((PX + PW//2 - 58, PY + 488), "INSTALL UPDATE", font=f_body_bold, fill=(10, 10, 12))
        
        # Closing Mascot
        draw_milo_mascot(draw, PX + PW//2, PY + 680, emotion="Happy", anim_time=t, size=90)
        draw_speech_bubble(draw, PX + 50, PY + 760, "Ready to be better than yesterday!", max_w=360)
        
        draw_bottom_nav(draw, PX, PY, PW, PH, "Profile")

    return img

if __name__ == "__main__":
    out_video = "/home/cipher/milo-android/milo_demo.mp4"
    if os.path.exists(out_video):
        os.remove(out_video)
        
    print(f"[*] Starting video generation: {WIDTH}x{HEIGHT} @ {FPS}fps, {TOTAL_SECONDS}s ({TOTAL_FRAMES} frames)...")
    
    cmd = [
        "/usr/bin/ffmpeg",
        "-y",
        "-f", "image2pipe",
        "-vcodec", "png",
        "-r", str(FPS),
        "-i", "-",
        "-c:v", "libx264",
        "-pix_fmt", "yuv420p",
        "-preset", "veryfast",
        "-crf", "20",
        out_video
    ]
    
    proc = subprocess.Popen(cmd, stdin=subprocess.PIPE)
    
    try:
        for idx in range(TOTAL_FRAMES):
            frame = render_frame(idx)
            frame.save(proc.stdin, format="PNG")
            if idx % 150 == 0:
                print(f"[*] Rendered {idx}/{TOTAL_FRAMES} frames ({int(idx/TOTAL_FRAMES*100)}%)...")
        proc.stdin.close()
        proc.wait()
        print(f"[✓] Successfully generated {out_video}!")
    except Exception as e:
        print(f"[!] Error: {e}", file=sys.stderr)
        proc.kill()
        sys.exit(1)
