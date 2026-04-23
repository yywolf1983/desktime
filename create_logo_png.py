#!/usr/bin/env python3
from PIL import Image, ImageDraw
import math

def create_logo_png():
    size = 512
    img = Image.new('RGBA', (size, size), (10, 10, 20, 255))
    draw = ImageDraw.Draw(img)
    
    center = size // 2
    
    # Colors
    gold = (255, 215, 0)
    gold_orange = (255, 165, 0)
    silver = (200, 200, 200)
    light_blue = (135, 206, 235)
    pale_blue = (173, 216, 230)
    
    def draw_gradient_circle(x, y, r, color1, color2):
        for i in range(r, 0, -1):
            ratio = i / r
            r_val = int(color1[0] * ratio + color2[0] * (1 - ratio))
            g_val = int(color1[1] * ratio + color2[1] * (1 - ratio))
            b_val = int(color1[2] * ratio + color2[2] * (1 - ratio))
            draw.ellipse([x-i, y-i, x+i, y+i], fill=(r_val, g_val, b_val))
    
    # Outer circle
    draw.ellipse([center-200, center-200, center+200, center+200], 
                 outline=gold, width=3)
    
    # Inner decorative circles
    draw.ellipse([center-180, center-180, center+180, center+180], 
                 outline=light_blue, width=1)
    draw.ellipse([center-160, center-160, center+160, center+160], 
                 outline=light_blue, width=1)
    
    # Nine Palace Grid
    grid_size = 160
    grid_x = center - grid_size // 2
    grid_y = center - grid_size // 2
    cell = grid_size // 3
    
    # Grid rectangle
    draw.rectangle([grid_x, grid_y, grid_x + grid_size, grid_y + grid_size], 
                   outline=silver, width=2)
    
    # Grid lines
    for i in range(1, 3):
        draw.line([grid_x + i * cell, grid_y, grid_x + i * cell, grid_y + grid_size], 
                  fill=silver, width=1)
        draw.line([grid_x, grid_y + i * cell, grid_x + grid_size, grid_y + i * cell], 
                  fill=silver, width=1)
    
    # Center golden dot
    draw_gradient_circle(center, center, 10, gold, gold_orange)
    
    # Eight positions (N, E, S, W and corners)
    positions = [
        (center, grid_y + cell // 2),  # North
        (grid_x + grid_size - cell // 2, center),  # East
        (center, grid_y + grid_size - cell // 2),  # South
        (grid_x + cell // 2, center),  # West
        (grid_x + cell // 2, grid_y + cell // 2),  # NW
        (grid_x + grid_size - cell // 2, grid_y + cell // 2),  # NE
        (grid_x + cell // 2, grid_y + grid_size - cell // 2),  # SW
        (grid_x + grid_size - cell // 2, grid_y + grid_size - cell // 2),  # SE
    ]
    
    for i, (px, py) in enumerate(positions):
        if i < 4:
            r = 5
            color = light_blue
        else:
            r = 4
            color = pale_blue
        draw.ellipse([px-r, py-r, px+r, py+r], fill=color)
    
    # Clock hands
    # Hour hand
    angle_hour = -30
    rad_hour = math.radians(angle_hour - 90)
    hour_len = 60
    hour_end_x = center + hour_len * math.cos(rad_hour)
    hour_end_y = center + hour_len * math.sin(rad_hour)
    draw.line([center, center, hour_end_x, hour_end_y], fill=gold, width=4)
    
    # Minute hand
    angle_min = 60
    rad_min = math.radians(angle_min - 90)
    min_len = 90
    min_end_x = center + min_len * math.cos(rad_min)
    min_end_y = center + min_len * math.sin(rad_min)
    draw.line([center, center, min_end_x, min_end_y], fill=silver, width=2)
    
    # Center dot
    draw_gradient_circle(center, center, 8, gold, gold_orange)
    
    # Outer arc decorations
    arc_rect1 = [center-200, center-200, center+200, center+200]
    draw.arc(arc_rect1, 0, 90, fill=gold, width=3)
    draw.arc(arc_rect1, 180, 270, fill=gold, width=3)
    
    # Top and bottom accent dots
    draw.ellipse([center-5, 130-5, center+5, 130+5], fill=light_blue)
    draw.ellipse([center-5, 382-5, center+5, 382+5], fill=gold)
    
    # Save
    img.save('/Users/yy/pro-test/time2/logo.png', 'PNG')
    print("Logo PNG created: logo.png")
    
    # Create various sizes for Android
    sizes = {
        'mipmap-mdpi': 48,
        'mipmap-hdpi': 72,
        'mipmap-xhdpi': 96,
        'mipmap-xxhdpi': 144,
        'mipmap-xxxhdpi': 192,
    }
    
    for folder, icon_size in sizes.items():
        resized = img.resize((icon_size, icon_size), Image.Resampling.LANCZOS)
        output_path = f'/Users/yy/pro-test/time2/app/src/main/res/{folder}/ic_launcher.png'
        resized.save(output_path, 'PNG')
        print(f"Created: {folder}/ic_launcher.png ({icon_size}x{icon_size})")
    
    # Create round icon
    for folder, icon_size in sizes.items():
        round_size = icon_size
        round_img = Image.new('RGBA', (round_size, round_size), (0, 0, 0, 0))
        mask = Image.new('L', (round_size, round_size), 0)
        mask_draw = ImageDraw.Draw(mask)
        mask_draw.ellipse([0, 0, round_size-1, round_size-1], fill=255)
        
        resized = img.resize((round_size, round_size), Image.Resampling.LANCZOS)
        round_img.paste(resized, mask=mask)
        
        output_path = f'/Users/yy/pro-test/time2/app/src/main/res/{folder}/ic_launcher_round.png'
        round_img.save(output_path, 'PNG')
        print(f"Created: {folder}/ic_launcher_round.png ({round_size}x{round_size})")

if __name__ == "__main__":
    create_logo_png()
