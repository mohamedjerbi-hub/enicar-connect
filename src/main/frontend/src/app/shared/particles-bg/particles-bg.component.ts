import { Component, OnInit, OnDestroy, ElementRef, ViewChild, inject } from '@angular/core';
import { ThemeService } from '../../core/services/theme.service';

@Component({
    selector: 'app-particles-bg',
    standalone: true,
    templateUrl: './particles-bg.component.html',
    styleUrl: './particles-bg.component.css'
})
export class ParticlesBgComponent implements OnInit, OnDestroy {
    @ViewChild('canvas', { static: true }) canvasRef!: ElementRef<HTMLCanvasElement>;
    private theme = inject(ThemeService);
    private animId = 0;

    ngOnInit(): void { this.init(); }
    ngOnDestroy(): void { cancelAnimationFrame(this.animId); window.removeEventListener('resize', this.resize); }

    private resize = () => {
        const c = this.canvasRef.nativeElement;
        c.width = window.innerWidth;
        c.height = window.innerHeight;
    };

    private init(): void {
        const canvas = this.canvasRef.nativeElement;
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        window.addEventListener('resize', this.resize);
        const ctx = canvas.getContext('2d')!;
        const COUNT = 55;

        interface P { x: number; y: number; vx: number; vy: number; r: number; }
        const particles: P[] = Array.from({ length: COUNT }, () => ({
            x: Math.random() * canvas.width,
            y: Math.random() * canvas.height,
            vx: (Math.random() - .5) * .5,
            vy: (Math.random() - .5) * .5,
            r: Math.random() * 1.8 + .6
        }));

        const draw = () => {
            const isDark = this.theme.theme() === 'dark';
            const color = isDark ? 'rgba(201,168,76,' : 'rgba(13,31,60,';
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            for (const p of particles) {
                p.x += p.vx; p.y += p.vy;
                if (p.x < 0 || p.x > canvas.width) p.vx *= -1;
                if (p.y < 0 || p.y > canvas.height) p.vy *= -1;
                ctx.beginPath();
                ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
                ctx.fillStyle = color + (isDark ? '.5)' : '.25)');
                ctx.fill();
                for (const q of particles) {
                    const dx = p.x - q.x, dy = p.y - q.y, d = Math.sqrt(dx * dx + dy * dy);
                    if (d < 120) {
                        ctx.beginPath();
                        ctx.moveTo(p.x, p.y); ctx.lineTo(q.x, q.y);
                        ctx.strokeStyle = color + ((1 - d / 120) * (isDark ? .18 : .09)) + ')';
                        ctx.lineWidth = .8;
                        ctx.stroke();
                    }
                }
            }
            this.animId = requestAnimationFrame(draw);
        };
        draw();
    }
}
