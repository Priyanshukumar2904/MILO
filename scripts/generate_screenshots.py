#!/usr/bin/env python3
"""
MILO Screenshot & Animated GIF Generator
Generates high-resolution, pixel-perfect screenshots and lightweight demonstration GIF.
"""

import math
import os
import sys
from PIL import Image, ImageDraw, ImageFont

# Dimensions for Phone Mockup Screenshots
SW = 520
SH = 1060

# Palette Definitions
# Dark Mode (Default)
BG_DARK = (14, 14, 16)
CARD_BG_DARK = (24, 24, 28)
CARD_BORDER_DARK = (40, 40, 48)
TEXT_WHITE = (252, 252, 254)
TEXT_MUTED_DARK = (165, 165, 175)
TEXT_DARK = (100, 100, 110)
DIVIDER_DARK = (32, 32, 38)
ACCENT_GREEN = (52, 199, 89)
ACCENT_AMBER = (255, 179, 64)

# Light Mode
BG_LIGHT = (248, 248, 250)
CARD_BG_LIGHT = (255, 255, 255)
CARD_BORDER_LIGHT = (228, 228, 234)
TEXT_BLACK = (18, 18, 20)
TEXT_MUTED_LIGHT = (110, 110, 120)
DIVIDER_LIGHT = (235, 235, 240)

FONT_BOLD = "/usr/share/fonts/TTF/DejaVuSans-Bold.ttf"
FONT_REG = "/usr/share/fonts/TTF/DejaVuSans.ttf"

def f(path, size):
    try:
        return ImageFont.truetype(path, size)
    except Exception:
        return ImageFont.load_default()

f_title = f(FONT_BOLD, 26)
f_h2 = f(FONT_BOLD, 20)
f_h3 = f(FONT_BOLD, 15)
f_body = f(FONT_REG, 13)
f_body_bold = f(FONT_BOLD, 13)
f_small = f(FONT_REG, 11)
f_tiny = f(FONT_REG, 9)
f_score = f(FONT_BOLD, 48)
f_timer = f(FONT_BOLD, 54)

def draw_phone_shell(draw, is_dark=True):
    bg_color = BG_DARK if is_dark else BG_LIGHT
    text_color = TEXT_WHITE if is_dark else TEXT_BLACK
    muted_color = TEXT_MUTED_DARK if is_dark else TEXT_MUTED_LIGHT
    border_color = (60, 60, 68) if is_dark else (210, 210, 220)
    
    # Outer device bezel
    draw.rounded_rectangle([0, 0, SW - 1, SH - 1], radius=42, fill=bg_color, outline=border_color, width=3)
    
    # Status bar
    draw.text((36, 18), "09:41", font=f_small, fill=text_color)
    draw.text((SW - 108, 18), "5G  100%", font=f_small, fill=muted_color)
    # Camera punch hole
    draw.ellipse([SW // 2 - 6, 16, SW // 2 + 6, 28], fill=(8, 8, 10))
    # Bottom gesture bar
    bar_col = (110, 110, 120) if is_dark else (180, 180, 190)
    draw.rounded_rectangle([SW // 2 - 50, SH - 14, SW // 2 + 50, SH - 10], radius=2, fill=bar_col)

def draw_bottom_nav(draw, active_tab="Today", is_dark=True):
    bar_h = 66
    by = SH - bar_h - 18
    nav_bg = (18, 18, 22) if is_dark else (255, 255, 255)
    div_col = DIVIDER_DARK if is_dark else DIVIDER_LIGHT
    draw.rectangle([0, by, SW, SH - 16], fill=nav_bg)
    draw.line([0, by, SW, by], fill=div_col, width=1)
    
    tabs = ["Today", "Schedule", "Habits", "Insights", "Profile"]
    tab_w = SW // len(tabs)
    for i, t in enumerate(tabs):
        tx = i * tab_w + tab_w // 2
        is_active = (t == active_tab)
        color = (TEXT_WHITE if is_dark else TEXT_BLACK) if is_active else (TEXT_DARK if is_dark else TEXT_MUTED_LIGHT)
        if is_active:
            pill_col = (36, 36, 44) if is_dark else (238, 238, 242)
            draw.ellipse([tx - 12, by + 12, tx + 12, by + 34], fill=pill_col)
        draw.text((tx - len(t)*3 - 3, by + 40), t, font=f_tiny, fill=color)

def draw_cat(draw, cx, cy, emotion="Calm", anim_t=0.0, size=65, is_dark=True):
    w = size
    h = int(size * 0.88)
    cat_fill = (235, 235, 242) if is_dark else (35, 35, 40)
    cat_line = (255, 255, 255) if is_dark else (20, 20, 25)
    ear_inner = (195, 195, 205) if is_dark else (65, 65, 75)
    eye_col = (20, 20, 25) if is_dark else (250, 250, 250)
    whisker_col = (160, 160, 170) if is_dark else (100, 100, 110)
    
    # Tail
    tail_swing = math.sin(anim_t * 5.0) * 8
    draw.line([cx - w//2 + 6, cy + h//2 - 6, cx - w//2 - 14 + int(tail_swing), cy + h//2 - 18], fill=whisker_col, width=3)
    
    # Head & Body
    draw.ellipse([cx - w//2, cy - h//2, cx + w//2, cy + h//2], fill=cat_fill, outline=cat_line, width=2)
    
    # Ears
    ear_w = int(w * 0.28)
    ear_h = int(h * 0.42)
    twitch = math.sin(anim_t * 6.0) * (3 if emotion in ["Curious", "Happy"] else 0)
    # Left Ear
    draw.polygon([(cx - w//2 + 6, cy - h//2 + 10), (cx - w//2 + ear_w//2, cy - h//2 - ear_h + int(twitch)), (cx - w//2 + ear_w, cy - h//2 + 2)], fill=cat_fill, outline=cat_line)
    draw.polygon([(cx - w//2 + 9, cy - h//2 + 8), (cx - w//2 + ear_w//2, cy - h//2 - ear_h + 6 + int(twitch)), (cx - w//2 + ear_w - 3, cy - h//2 + 4)], fill=ear_inner)
    # Right Ear
    draw.polygon([(cx + w//2 - ear_w, cy - h//2 + 2), (cx + w//2 - ear_w//2, cy - h//2 - ear_h - int(twitch)), (cx + w//2 - 6, cy - h//2 + 10)], fill=cat_fill, outline=cat_line)
    draw.polygon([(cx + w//2 - ear_w + 3, cy - h//2 + 4), (cx + w//2 - ear_w//2, cy - h//2 - ear_h + 6 - int(twitch)), (cx + w//2 - 9, cy - h//2 + 8)], fill=ear_inner)
    
    # Eyes
    eye_offset = int(w * 0.22)
    eye_y = cy - int(h * 0.08)
    if emotion in ["Sleepy", "Happy", "Celebrating"]:
        draw.arc([cx - eye_offset - 7, eye_y - 6, cx - eye_offset + 7, eye_y + 7], start=200, end=340, fill=eye_col, width=3)
        draw.arc([cx + eye_offset - 7, eye_y - 6, cx + eye_offset + 7, eye_y + 7], start=200, end=340, fill=eye_col, width=3)
    else:
        draw.ellipse([cx - eye_offset - 5, eye_y - 5, cx - eye_offset + 5, eye_y + 5], fill=eye_col)
        draw.ellipse([cx + eye_offset - 5, eye_y - 5, cx + eye_offset + 5, eye_y + 5], fill=eye_col)
        # Highlight
        draw.ellipse([cx - eye_offset - 3, eye_y - 3, cx - eye_offset, eye_y], fill=(255, 255, 255) if is_dark else (20, 20, 20))
        draw.ellipse([cx + eye_offset - 3, eye_y - 3, cx + eye_offset, eye_y], fill=(255, 255, 255) if is_dark else (20, 20, 20))
        
    # Whiskers
    draw.line([cx - w//2 + 4, cy + 2, cx - eye_offset - 8, cy], fill=whisker_col, width=1)
    draw.line([cx - w//2 + 2, cy + 7, cx - eye_offset - 8, cy + 8], fill=whisker_col, width=1)
    draw.line([cx + eye_offset + 8, cy, cx + w//2 - 4, cy + 2], fill=whisker_col, width=1)
    draw.line([cx + eye_offset + 8, cy + 8, cx + w//2 - 2, cy + 7], fill=whisker_col, width=1)
    
    # Mouth
    draw.polygon([(cx - 2, cy + 3), (cx + 2, cy + 3), (cx, cy + 6)], fill=(60, 60, 65))
    draw.arc([cx - 7, cy + 4, cx, cy + 10], start=20, end=160, fill=(60, 60, 65), width=2)
    draw.arc([cx, cy + 4, cx + 7, cy + 10], start=20, end=160, fill=(60, 60, 65), width=2)
    
    # Paws
    paw_y = cy + h//2 - 4
    if emotion in ["Celebrating", "Welcoming"]:
        draw.ellipse([cx + w//2 - 10, cy - 8, cx + w//2 + 4, cy + 8], fill=cat_fill, outline=cat_line)
        draw.ellipse([cx - w//3 - 6, paw_y - 5, cx - w//3 + 6, paw_y + 5], fill=cat_fill, outline=cat_line)
    else:
        draw.ellipse([cx - w//4 - 6, paw_y - 5, cx - w//4 + 6, paw_y + 5], fill=cat_fill, outline=cat_line)
        draw.ellipse([cx + w//4 - 6, paw_y - 5, cx + w//4 + 6, paw_y + 5], fill=cat_fill, outline=cat_line)

def draw_speech(draw, x, y, text, is_dark=True):
    tw = len(text) * 7 + 22
    bg = (32, 32, 38) if is_dark else (240, 240, 246)
    border = (50, 50, 60) if is_dark else (215, 215, 225)
    tc = TEXT_WHITE if is_dark else TEXT_BLACK
    draw.rounded_rectangle([x, y, x + tw, y + 34], radius=10, fill=bg, outline=border)
    draw.polygon([(x - 6, y + 15), (x, y + 10), (x, y + 20)], fill=bg)
    draw.text((x + 10, y + 10), text, font=f_small, fill=tc)

# ==========================================
# 1. SCREEN: TODAY (DARK & LIGHT)
# ==========================================
def render_today(is_dark=True):
    img = Image.new("RGB", (SW, SH), BG_DARK if is_dark else BG_LIGHT)
    draw = ImageDraw.Draw(img)
    draw_phone_shell(draw, is_dark)
    
    card_bg = CARD_BG_DARK if is_dark else CARD_BG_LIGHT
    card_border = CARD_BORDER_DARK if is_dark else CARD_BORDER_LIGHT
    tc = TEXT_WHITE if is_dark else TEXT_BLACK
    tm = TEXT_MUTED_DARK if is_dark else TEXT_MUTED_LIGHT
    
    # App Bar
    draw.text((28, 56), "TODAY", font=f_title, fill=tc)
    draw.text((28, 88), "Saturday, Sep 5  •  Day 24 Streak", font=f_small, fill=tm)
    draw_cat(draw, SW - 65, 78, emotion="Happy", size=48, is_dark=is_dark)
    
    # Score Card
    draw.rounded_rectangle([24, 118, SW - 24, 345], radius=20, fill=card_bg, outline=card_border)
    
    # Score Arc
    cx, cy, r = SW // 2, 210, 66
    arc_bg = (45, 45, 52) if is_dark else (225, 225, 232)
    arc_fg = (255, 255, 255) if is_dark else (20, 20, 24)
    draw.arc([cx - r, cy - r, cx + r, cy + r], start=0, end=360, fill=arc_bg, width=10)
    sweep = int(360 * 0.78)
    draw.arc([cx - r, cy - r, cx + r, cy + r], start=-90, end=-90 + sweep, fill=arc_fg, width=10)
    
    draw.text((cx - 30, cy - 30), "78", font=f_score, fill=tc)
    draw.text((cx - 16, cy + 20), "/ 100", font=f_tiny, fill=tm)
    
    # Delta badge
    badge_bg = (30, 48, 36) if is_dark else (220, 245, 226)
    draw.rounded_rectangle([cx - 46, 302, cx + 46, 328], radius=12, fill=badge_bg, outline=(42, 80, 52) if is_dark else (160, 220, 175))
    draw.text((cx - 36, 308), "▲ +9% vs yest", font=f_tiny, fill=ACCENT_GREEN)
    
    # Metric Chips
    chips = [("Tasks", "82%"), ("Focus", "88%"), ("Habits", "84%"), ("Routine", "80%")]
    mx = 24
    cw = (SW - 48 - 24) // 4
    for label, val in chips:
        draw.rounded_rectangle([mx, 360, mx + cw, 405], radius=12, fill=card_bg, outline=card_border)
        draw.text((mx + 10, 368), label, font=f_tiny, fill=tm)
        draw.text((mx + 10, 384), val, font=f_h3, fill=tc)
        mx += cw + 8
        
    # Timeline
    draw.text((28, 425), "DAILY TIMELINE", font=f_small, fill=tm)
    
    # Node 1 (Completed)
    draw.rounded_rectangle([24, 448, SW - 24, 518], radius=14, fill=card_bg, outline=card_border)
    draw.ellipse([38, 468, 56, 486], fill=ACCENT_GREEN)
    draw.text((42, 470), "✓", font=f_tiny, fill=(10, 10, 12))
    draw.text((72, 460), "Morning Deep Work", font=f_body_bold, fill=tc)
    draw.text((72, 484), "08:00 – 10:00  •  Deep Focus  •  120m", font=f_small, fill=tm)
    
    # Node 2 (Live Tracking)
    live_border = (180, 180, 190) if is_dark else (40, 40, 45)
    draw.rounded_rectangle([24, 532, SW - 24, 616], radius=14, fill=(30, 30, 38) if is_dark else (244, 244, 250), outline=live_border, width=2)
    draw.ellipse([40, 558, 54, 572], fill=tc)
    draw.text((72, 544), "System Architecture Sync", font=f_body_bold, fill=tc)
    draw.text((72, 568), "10:30 – 12:00  •  LIVE TRACKING", font=f_small, fill=ACCENT_GREEN)
    draw.text((72, 588), "Elapsed: 42m / 90m planned", font=f_tiny, fill=tm)
    
    # Node 3 (Upcoming)
    draw.rounded_rectangle([24, 630, SW - 24, 696], radius=14, fill=card_bg, outline=card_border)
    draw.ellipse([40, 652, 54, 666], outline=tm, width=2)
    draw.text((72, 642), "Algorithm Practice & Review", font=f_body_bold, fill=tm)
    draw.text((72, 666), "14:00 – 15:30  •  Study Block", font=f_small, fill=tm)
    
    # Habits Strip
    draw.text((28, 715), "HABITS TODAY (2 OF 3 DONE)", font=f_small, fill=tm)
    draw.rounded_rectangle([24, 735, SW - 24, 792], radius=14, fill=card_bg, outline=card_border)
    draw.text((42, 752), "Morning Run  ✓", font=f_small, fill=ACCENT_GREEN)
    draw.text((200, 752), "Hydration  ✓", font=f_small, fill=ACCENT_GREEN)
    draw.text((345, 752), "Reading  ○", font=f_small, fill=tm)
    
    # Mascot Coach Strip
    draw.rounded_rectangle([24, 808, SW - 24, 888], radius=16, fill=card_bg, outline=card_border)
    draw_cat(draw, 65, 848, emotion="Encouraging", size=50, is_dark=is_dark)
    draw_speech(draw, 115, 832, "You're 9% ahead of yesterday. Keep flowing!", is_dark=is_dark)
    
    draw_bottom_nav(draw, "Today", is_dark)
    return img

# ==========================================
# 2. SCREEN: SCHEDULE
# ==========================================
def render_schedule():
    img = Image.new("RGB", (SW, SH), BG_DARK)
    draw = ImageDraw.Draw(img)
    draw_phone_shell(draw, True)
    
    draw.text((28, 56), "SCHEDULE", font=f_title, fill=TEXT_WHITE)
    draw.text((28, 88), "Intentional Time Blocking", font=f_small, fill=TEXT_MUTED_DARK)
    
    # Day / Week / Month Switcher
    draw.rounded_rectangle([24, 116, SW - 24, 158], radius=12, fill=(22, 22, 26), outline=CARD_BORDER_DARK)
    draw.rounded_rectangle([28, 120, 168, 154], radius=9, fill=TEXT_WHITE)
    draw.text((78, 128), "Day", font=f_body_bold, fill=(10, 10, 12))
    draw.text((230, 128), "Week", font=f_body, fill=TEXT_MUTED_DARK)
    draw.text((375, 128), "Month", font=f_body, fill=TEXT_MUTED_DARK)
    
    items = [
        ("08:00 – 10:00", "Deep Work: Jetpack Compose Engine", "DEEP WORK", True),
        ("10:30 – 11:30", "Team Architecture & Code Review", "WORK", True),
        ("12:00 – 13:00", "Nutritious Lunch & Sunshine Walk", "HEALTH", True),
        ("13:30 – 15:30", "Documentation & Video Pipeline", "DEEP WORK", False),
        ("16:00 – 17:00", "Gym & Cardio HIIT Session", "EXERCISE", False),
        ("19:00 – 20:00", "Evening Reading & Reflection", "ROUTINE", False),
        ("21:30 – 22:30", "Wind-down & Offline Sleep Prep", "RECOVERY", False)
    ]
    
    sy = 175
    for time_str, title, cat, done in items:
        draw.rounded_rectangle([24, sy, SW - 24, sy + 70], radius=14, fill=CARD_BG_DARK, outline=CARD_BORDER_DARK)
        # Category Tag
        draw.rounded_rectangle([38, sy + 12, 138, sy + 32], radius=6, fill=(34, 34, 42))
        draw.text((46, sy + 16), cat, font=f_tiny, fill=TEXT_WHITE)
        draw.text((150, sy + 16), time_str, font=f_tiny, fill=TEXT_MUTED_DARK)
        draw.text((38, sy + 40), title, font=f_body_bold, fill=TEXT_WHITE if not done else TEXT_MUTED_DARK)
        if done:
            draw.text((SW - 56, sy + 24), "✓", font=f_h2, fill=ACCENT_GREEN)
        else:
            draw.text((SW - 56, sy + 24), "○", font=f_h2, fill=TEXT_MUTED_DARK)
        sy += 78
        
    draw_bottom_nav(draw, "Schedule", True)
    return img

# ==========================================
# 3. SCREEN: HABITS
# ==========================================
def render_habits():
    img = Image.new("RGB", (SW, SH), BG_DARK)
    draw = ImageDraw.Draw(img)
    draw_phone_shell(draw, True)
    
    draw.text((28, 56), "HABIT MATRIX", font=f_title, fill=TEXT_WHITE)
    draw.text((28, 88), "Consistency Over Perfection", font=f_small, fill=TEXT_MUTED_DARK)
    
    habits = [
        ("Morning Run & Cardio", "12 Day Streak", "94% Consistency", [True, True, True, True, True, True, True]),
        ("Deep Reading (20m+)", "7 Day Streak", "88% Consistency", [True, True, True, False, True, True, True]),
        ("Code Practice & Kata", "18 Day Streak (PR!)", "96% Consistency", [True, True, True, True, True, True, True]),
        ("Meditation / Mindfulness", "4 Day Streak", "78% Consistency", [True, False, True, True, True, False, True])
    ]
    
    hy = 120
    days = ["M", "T", "W", "T", "F", "S", "S"]
    for title, streak, cons, matrix in habits:
        draw.rounded_rectangle([24, hy, SW - 24, hy + 132], radius=16, fill=CARD_BG_DARK, outline=CARD_BORDER_DARK)
        draw.text((38, hy + 16), title, font=f_body_bold, fill=TEXT_WHITE)
        draw.text((38, hy + 38), f"{streak}  •  {cons}", font=f_tiny, fill=ACCENT_GREEN if "PR" in streak or "12" in streak else TEXT_MUTED_DARK)
        
        # 7-day checkboxes
        mx = 38
        cw = (SW - 76 - 36) // 7
        for i in range(7):
            draw.text((mx + 10, hy + 62), days[i], font=f_tiny, fill=TEXT_MUTED_DARK)
            chk = matrix[i]
            box_fill = TEXT_WHITE if chk else (34, 34, 42)
            draw.rounded_rectangle([mx, hy + 80, mx + cw, hy + 116], radius=7, fill=box_fill)
            if chk:
                draw.text((mx + 10, hy + 90), "✓", font=f_tiny, fill=(10, 10, 12))
            mx += cw + 6
        hy += 144
        
    # Behavioral insight
    draw.rounded_rectangle([24, hy + 10, SW - 24, hy + 135], radius=16, fill=(28, 28, 36), outline=(48, 48, 60))
    draw.text((38, hy + 26), "BEHAVIORAL VELOCITY", font=f_tiny, fill=ACCENT_AMBER)
    draw.text((38, hy + 50), "“Based on the last 4 weeks,", font=f_body, fill=TEXT_WHITE)
    draw.text((38, hy + 72), "your habit streak velocity is up +15.8%.”", font=f_body_bold, fill=ACCENT_GREEN)
    draw.text((38, hy + 98), "Sample: 28 days tracked • 84% adherence", font=f_tiny, fill=TEXT_MUTED_DARK)
    
    draw_bottom_nav(draw, "Habits", True)
    return img

# ==========================================
# 4. SCREEN: INSIGHTS
# ==========================================
def render_insights():
    img = Image.new("RGB", (SW, SH), BG_DARK)
    draw = ImageDraw.Draw(img)
    draw_phone_shell(draw, True)
    
    draw.text((28, 56), "INSIGHTS", font=f_title, fill=TEXT_WHITE)
    draw.text((28, 88), "Data-Driven Behavioral Analytics", font=f_small, fill=TEXT_MUTED_DARK)
    
    # Peak Window Card
    draw.rounded_rectangle([24, 118, SW - 24, 215], radius=16, fill=CARD_BG_DARK, outline=CARD_BORDER_DARK)
    draw.text((38, 136), "PEAK FOCUS WINDOW", font=f_tiny, fill=ACCENT_AMBER)
    draw.text((38, 160), "9:00 AM – 12:00 PM", font=f_h2, fill=TEXT_WHITE)
    draw.text((38, 186), "Productivity score averages 92/100 during morning blocks.", font=f_small, fill=TEXT_MUTED_DARK)
    
    # Trend Cards
    trends = [
        ("Productivity Score", "78 / 100", "+9.8% vs last week", True),
        ("Deep Work Hours", "37h logged", "+23% vs last month", True),
        ("Screen Distraction", "1h 15m", "-24% leisure reduction", True),
        ("Sleep Schedule", "7h 20m avg", "Bedtime variance ±18m", False)
    ]
    
    ty = 230
    for name, stat, delta, positive in trends:
        draw.rounded_rectangle([24, ty, SW - 24, ty + 78], radius=14, fill=CARD_BG_DARK, outline=CARD_BORDER_DARK)
        draw.text((38, ty + 16), name, font=f_small, fill=TEXT_MUTED_DARK)
        draw.text((38, ty + 38), stat, font=f_h3, fill=TEXT_WHITE)
        draw.text((SW - 185, ty + 38), delta, font=f_small, fill=ACCENT_GREEN if positive else TEXT_MUTED_DARK)
        ty += 88
        
    # Category scorecard
    draw.text((28, ty + 10), "CATEGORY PERFORMANCE SCORECARD", font=f_tiny, fill=TEXT_MUTED_DARK)
    cats = [("Fitness", 91), ("Deep Study", 84), ("Routines", 83), ("Engineering Work", 77)]
    cy = ty + 32
    for cname, score in cats:
        draw.rounded_rectangle([24, cy, SW - 24, cy + 44], radius=10, fill=CARD_BG_DARK, outline=CARD_BORDER_DARK)
        draw.text((38, cy + 14), cname, font=f_body_bold, fill=TEXT_WHITE)
        draw.text((SW - 65, cy + 14), f"{score}", font=f_body_bold, fill=ACCENT_GREEN if score >= 80 else TEXT_WHITE)
        cy += 52
        
    draw_bottom_nav(draw, "Insights", True)
    return img

# ==========================================
# 5. SCREEN: WEEKLY REPORT
# ==========================================
def render_weekly_report():
    img = Image.new("RGB", (SW, SH), BG_DARK)
    draw = ImageDraw.Draw(img)
    draw_phone_shell(draw, True)
    
    draw.text((28, 56), "WEEKLY REPORT", font=f_title, fill=TEXT_WHITE)
    draw.text((28, 88), "Aug 30 – Sep 5, 2026", font=f_small, fill=TEXT_MUTED_DARK)
    
    # Top score card
    draw.rounded_rectangle([24, 118, SW - 24, 215], radius=16, fill=CARD_BG_DARK, outline=CARD_BORDER_DARK)
    draw.text((38, 136), "AVERAGE SCORE", font=f_tiny, fill=TEXT_MUTED_DARK)
    draw.text((38, 158), "82 / 100", font=f_score, fill=TEXT_WHITE)
    draw.rounded_rectangle([SW - 160, 158, SW - 38, 192], radius=10, fill=(30, 48, 36), outline=(42, 80, 52))
    draw.text((SW - 146, 168), "▲ +8% vs prev", font=f_tiny, fill=ACCENT_GREEN)
    
    # 7-Day Bar Chart
    draw.text((28, 235), "DAILY PRODUCTIVITY DISTRIBUTION", font=f_tiny, fill=TEXT_MUTED_DARK)
    draw.rounded_rectangle([24, 255, SW - 24, 450], radius=16, fill=CARD_BG_DARK, outline=CARD_BORDER_DARK)
    
    days = [("M", 74), ("T", 80), ("W", 78), ("T", 85), ("F", 88), ("S", 92), ("S", 84)]
    bw = 32
    start_x = 45
    spacing = (SW - 90 - 7 * bw) // 6
    chart_base_y = 410
    max_h = 110
    
    for i, (dname, val) in enumerate(days):
        x = start_x + i * (bw + spacing)
        bar_h = int((val / 100.0) * max_h)
        # Bar fill
        draw.rounded_rectangle([x, chart_base_y - bar_h, x + bw, chart_base_y], radius=6, fill=TEXT_WHITE if val >= 85 else (70, 70, 80))
        draw.text((x + 8, chart_base_y - bar_h - 18), str(val), font=f_tiny, fill=TEXT_MUTED_DARK)
        draw.text((x + 10, chart_base_y + 10), dname, font=f_small, fill=TEXT_WHITE)
        
    # Story Narrative
    draw.rounded_rectangle([24, 470, SW - 24, 610], radius=16, fill=CARD_BG_DARK, outline=CARD_BORDER_DARK)
    draw.text((38, 488), "WEEKLY RETROSPECTIVE", font=f_tiny, fill=ACCENT_AMBER)
    draw.text((38, 514), "“Your focus stamina and deep study blocks", font=f_body, fill=TEXT_WHITE)
    draw.text((38, 536), "grew visibly in the second half of the week.", font=f_body, fill=TEXT_WHITE)
    draw.text((38, 558), "Morning anchors remained exceptionally steady.”", font=f_body_bold, fill=TEXT_WHITE)
    
    # Weekly Totals
    totals = [("34.5h", "Productive Flow"), ("18.2h", "Deep Study"), ("6", "Workouts"), ("88%", "Habit Rate")]
    tx = 24
    tw = (SW - 48 - 24) // 4
    for val, lab in totals:
        draw.rounded_rectangle([tx, 628, tx + tw, 695], radius=12, fill=CARD_BG_DARK, outline=CARD_BORDER_DARK)
        draw.text((tx + 10, 642), val, font=f_h3, fill=ACCENT_GREEN if "%" in val or "34" in val else TEXT_WHITE)
        draw.text((tx + 10, 668), lab, font=f_tiny, fill=TEXT_MUTED_DARK)
        tx += tw + 8
        
    draw_bottom_nav(draw, "Insights", True)
    return img

# ==========================================
# 6. SCREEN: LIFE REPORT
# ==========================================
def render_life_report():
    img = Image.new("RGB", (SW, SH), BG_DARK)
    draw = ImageDraw.Draw(img)
    draw_phone_shell(draw, True)
    
    draw.text((28, 56), "MONTHLY LIFE REPORT", font=f_title, fill=TEXT_WHITE)
    draw.text((28, 88), "September 2026 Retrospective", font=f_small, fill=TEXT_MUTED_DARK)
    
    # Signature Headline Card
    draw.rounded_rectangle([24, 118, SW - 24, 270], radius=18, fill=CARD_BG_DARK, outline=CARD_BORDER_DARK)
    draw.text((40, 140), "“You showed up.”", font=f_title, fill=TEXT_WHITE)
    draw.text((40, 180), "You completed 86% of your planned", font=f_body, fill=TEXT_MUTED_DARK)
    draw.text((40, 204), "activities this month. You are not", font=f_body, fill=TEXT_MUTED_DARK)
    draw.text((40, 228), "the same person who started this month.", font=f_body_bold, fill=TEXT_WHITE)
    
    # Stats Grid
    stats = [
        ("24", "Days Active", ACCENT_GREEN),
        ("125h", "Total Flow Time", TEXT_WHITE),
        ("37h", "Deep Study Blocks", TEXT_WHITE),
        ("14", "Gym Workouts", ACCENT_GREEN)
    ]
    grid_xy = [(24, 290), (SW//2 + 6, 290), (24, 385), (SW//2 + 6, 385)]
    gw = SW // 2 - 30
    for (val, lab, col), (gx, gy) in zip(stats, grid_xy):
        draw.rounded_rectangle([gx, gy, gx + gw, gy + 82], radius=14, fill=CARD_BG_DARK, outline=CARD_BORDER_DARK)
        draw.text((gx + 16, gy + 14), val, font=f_h2, fill=col)
        draw.text((gx + 16, gy + 50), lab, font=f_tiny, fill=TEXT_MUTED_DARK)
        
    # Celebrating Mascot
    cx, cy = SW // 2, 550
    for i in range(12):
        angle = i * (math.pi / 6)
        spx = cx + int(math.cos(angle) * 75)
        spy = cy + int(math.sin(angle) * 55)
        draw.point((spx, spy), fill=TEXT_WHITE)
        draw.point((spx + 1, spy), fill=TEXT_WHITE)
    draw_cat(draw, cx, cy, emotion="Celebrating", size=90, is_dark=True)
    draw_speech(draw, 50, 640, "Look at what you achieved this month!", is_dark=True)
    
    # Milestone card
    draw.rounded_rectangle([24, 705, SW - 24, 795], radius=14, fill=CARD_BG_DARK, outline=CARD_BORDER_DARK)
    draw.text((40, 722), "BIGGEST IMPROVEMENT", font=f_tiny, fill=ACCENT_AMBER)
    draw.text((40, 746), "Study Consistency (+23%)  •  Best Day: Sep 4 (92/100)", font=f_small, fill=TEXT_WHITE)
    
    draw_bottom_nav(draw, "Insights", True)
    return img

# ==========================================
# 7. SCREEN: ACHIEVEMENTS
# ==========================================
def render_achievements():
    img = Image.new("RGB", (SW, SH), BG_DARK)
    draw = ImageDraw.Draw(img)
    draw_phone_shell(draw, True)
    
    draw.text((28, 56), "ACHIEVEMENTS", font=f_title, fill=TEXT_WHITE)
    draw.text((28, 88), "Milestones Celebrating Real Progress", font=f_small, fill=TEXT_MUTED_DARK)
    
    achievements = [
        ("The Beginning", "Completed first planned day", "UNLOCKED", True),
        ("Steady Anchor", "3-day morning routine streak", "UNLOCKED", True),
        ("Unstoppable", "7 consecutive days tracking", "UNLOCKED", True),
        ("Centurion", "Logged 100 deep focus hours", "UNLOCKED", True),
        ("Resilient Mindset", "Returned after a rest day without guilt", "UNLOCKED", True),
        ("Stamina Master", "180-minute unbroken study flow", "IN PROGRESS", False),
        ("Master of Time", "100% adherence on 5 scheduled days", "IN PROGRESS", False)
    ]
    
    ay = 120
    for title, desc, status, unlocked in achievements:
        draw.rounded_rectangle([24, ay, SW - 24, ay + 74], radius=14, fill=CARD_BG_DARK, outline=CARD_BORDER_DARK)
        # Medal/Badge Icon
        badge_fill = TEXT_WHITE if unlocked else (34, 34, 40)
        draw.ellipse([38, ay + 18, 72, ay + 52], fill=badge_fill)
        draw.text((50, ay + 26), "★" if unlocked else "○", font=f_h3, fill=(10, 10, 12) if unlocked else TEXT_MUTED_DARK)
        draw.text((86, ay + 16), title, font=f_body_bold, fill=TEXT_WHITE if unlocked else TEXT_MUTED_DARK)
        draw.text((86, ay + 40), desc, font=f_tiny, fill=TEXT_MUTED_DARK)
        draw.text((SW - 110, ay + 28), status, font=f_tiny, fill=ACCENT_GREEN if unlocked else TEXT_DARK)
        ay += 84
        
    draw_bottom_nav(draw, "Profile", True)
    return img

# ==========================================
# 8. SCREEN: MILO MASCOT SHOWCASE
# ==========================================
def render_milo_mascot_showcase():
    img = Image.new("RGB", (SW, SH), BG_DARK)
    draw = ImageDraw.Draw(img)
    draw_phone_shell(draw, True)
    
    draw.text((28, 56), "MILO COMPANION", font=f_title, fill=TEXT_WHITE)
    draw.text((28, 88), "8 Expressive Emotional States", font=f_small, fill=TEXT_MUTED_DARK)
    
    emotions = [
        ("Calm", "Gentle breathing baseline"),
        ("Happy", "Smiling eyes & twitches"),
        ("Proud", "Sparkles & chest up"),
        ("Curious", "Head tilt & whiskering"),
        ("Encouraging", "Support on slow days"),
        ("Sleepy", "Evening wind-down"),
        ("Celebrating", "Raised paws & confetti"),
        ("Welcoming", "Waving paw greeting")
    ]
    
    # 2-column grid of mascot states
    grid_coords = [
        (24, 125), (SW//2 + 6, 125),
        (24, 290), (SW//2 + 6, 290),
        (24, 455), (SW//2 + 6, 455),
        (24, 620), (SW//2 + 6, 620)
    ]
    gw = SW // 2 - 30
    gh = 150
    
    for (em_name, desc), (gx, gy) in zip(emotions, grid_coords):
        draw.rounded_rectangle([gx, gy, gx + gw, gy + gh], radius=16, fill=CARD_BG_DARK, outline=CARD_BORDER_DARK)
        draw_cat(draw, gx + gw // 2, gy + 55, emotion=em_name, size=52, is_dark=True)
        draw.text((gx + gw//2 - len(em_name)*4, gy + 104), em_name, font=f_body_bold, fill=TEXT_WHITE)
        draw.text((gx + 12, gy + 126), desc, font=f_tiny, fill=TEXT_MUTED_DARK)
        
    draw_bottom_nav(draw, "Profile", True)
    return img

# ==========================================
# 9. HERO BANNER FOR GITHUB README
# ==========================================
def render_hero_banner():
    BW, BH = 1200, 520
    img = Image.new("RGB", (BW, BH), (10, 10, 12))
    draw = ImageDraw.Draw(img)
    
    # Ambient grid dots
    for x in range(0, BW, 40):
        for y in range(0, BH, 40):
            draw.point((x, y), fill=(24, 24, 28))
            
    # Left Hero Text
    draw.text((80, 80), "MILO 🐈", font=f(FONT_BOLD, 48), fill=TEXT_WHITE)
    draw.text((80, 150), "Your personal productivity companion.", font=f(FONT_REG, 22), fill=TEXT_MUTED_DARK)
    draw.text((80, 195), "“Track your day. Understand your habits.", font=f(FONT_REG, 17), fill=TEXT_WHITE)
    draw.text((80, 225), "See your progress. Become better than yesterday.”", font=f(FONT_BOLD, 17), fill=TEXT_WHITE)
    
    # Quick Badges
    badges = ["Native Android", "Kotlin 2.0", "Jetpack Compose", "100% Offline", "Zero Guilt"]
    bx = 80
    for b in badges:
        bw = len(b) * 8 + 24
        draw.rounded_rectangle([bx, 280, bx + bw, 314], radius=8, fill=(24, 24, 30), outline=(45, 45, 54))
        draw.text((bx + 12, 290), b, font=f_small, fill=TEXT_MUTED_DARK)
        bx += bw + 12
        
    # CTA Buttons mockup
    draw.rounded_rectangle([80, 345, 290, 395], radius=12, fill=TEXT_WHITE)
    draw.text((105, 360), "Download Android APK", font=f_body_bold, fill=(10, 10, 12))
    
    draw.rounded_rectangle([310, 345, 460, 395], radius=12, fill=(24, 24, 30), outline=(50, 50, 60))
    draw.text((345, 360), "View Demo", font=f_body_bold, fill=TEXT_WHITE)
    
    # Right Side: Mascot in big circle with score ring preview
    rcx, rcy = 920, 260
    draw.ellipse([rcx - 160, rcy - 160, rcx + 160, rcy + 160], fill=(18, 18, 22), outline=(36, 36, 44), width=2)
    # Score Arc
    draw.arc([rcx - 140, rcy - 140, rcx + 140, rcy + 140], start=-90, end=190, fill=TEXT_WHITE, width=8)
    draw_cat(draw, rcx, rcy, emotion="Happy", size=130, is_dark=True)
    draw_speech(draw, rcx - 120, rcy + 90, "Ready to be better than yesterday!", is_dark=True)
    
    return img

# ==========================================
# 10. ANIMATED DEMO GIF GENERATION
# ==========================================
def render_demo_gif():
    print("[*] Rendering demo GIF frames...")
    frames = []
    # Sequence: Today -> Schedule -> Habits -> Insights -> Life Report
    screens = [
        ("Today", render_today(True)),
        ("Schedule", render_schedule()),
        ("Habits", render_habits()),
        ("Insights", render_insights()),
        ("Life Report", render_life_report())
    ]
    
    for name, scr in screens:
        # Resize to lightweight dimensions for fast GitHub loading (260x530)
        thumb = scr.resize((260, 530), Image.Resampling.LANCZOS)
        # Duplicate each screen for ~1.5s pause
        for _ in range(3):
            frames.append(thumb)
            
    gif_path = "docs/demo/milo-demo.gif"
    frames[0].save(
        gif_path,
        save_all=True,
        append_images=frames[1:],
        duration=500,
        loop=0,
        optimize=True
    )
    print(f"[✓] Saved animated GIF to {gif_path}")

if __name__ == "__main__":
    os.makedirs("docs/screenshots", exist_ok=True)
    os.makedirs("docs/demo", exist_ok=True)
    
    print("[*] Generating high-resolution screenshots...")
    render_today(True).save("docs/screenshots/today-dark.png")
    render_today(False).save("docs/screenshots/today-light.png")
    render_schedule().save("docs/screenshots/schedule.png")
    render_habits().save("docs/screenshots/habits.png")
    render_insights().save("docs/screenshots/insights.png")
    render_weekly_report().save("docs/screenshots/weekly-report.png")
    render_life_report().save("docs/screenshots/life-report.png")
    render_achievements().save("docs/screenshots/achievements.png")
    render_milo_mascot_showcase().save("docs/screenshots/milo-mascot.png")
    render_hero_banner().save("docs/screenshots/hero-banner.png")
    print("[✓] All 10 screenshots generated in docs/screenshots/")
    
    render_demo_gif()
    print("[✓] Completed visual assets generation.")
