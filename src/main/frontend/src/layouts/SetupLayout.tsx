import {Outlet} from "react-router-dom";
import { useEffect, useRef } from 'react';

interface WaveConfig {
    y: number;
    length: number;
    amplitude: number;
    speed: number;
    color: string;
}

export default function SetupLayout() {

    const canvasRef = useRef<HTMLCanvasElement>(null);

    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;

        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        let animationFrameId: number;
        let width: number;
        let height: number;

        const resize = () => {
            width = canvas.width = window.innerWidth;
            height = canvas.height = window.innerHeight;
        };

        resize();
        window.addEventListener('resize', resize);

        const waves: WaveConfig[] = [
            { y: 0.7, length: 0.01, amplitude: 50, speed: 0.003, color: 'rgba(0, 152, 105, 0.1)' },
            { y: 0.75, length: 0.007, amplitude: 70, speed: 0.005, color: 'rgba(0, 152, 105, 0.15)' },
            { y: 0.8, length: 0.005, amplitude: 40, speed: 0.007, color: 'rgba(0, 152, 105, 0.2)' }
        ];

        let increment = 0;

        const draw = () => {
            ctx.clearRect(0, 0, width, height);

            // Hintergrundfarbe
            ctx.fillStyle = '#090909';
            ctx.fillRect(0, 0, width, height);

            increment += 1;

            waves.forEach((wave) => {
                ctx.beginPath();

                ctx.moveTo(0, height * wave.y);

                for (let i = 0; i < width; i++) {
                    ctx.lineTo(
                        i,
                        height * wave.y +
                        Math.sin(i * wave.length + increment * wave.speed) * wave.amplitude
                    );
                }

                ctx.lineTo(width, height);
                ctx.lineTo(0, height);
                ctx.fillStyle = wave.color;
                ctx.fill();
            });

            animationFrameId = window.requestAnimationFrame(draw);
        };

        draw();

        return () => {
            window.removeEventListener('resize', resize);
            window.cancelAnimationFrame(animationFrameId);
        };
    }, []);

    return (
        <div className="relative w-full h-screen overflow-hidden font-sans text-white">
            <canvas
                ref={canvasRef}
                className="absolute top-0 left-0 w-full h-full z-0 pointer-events-none"
            />
            <div className="relative z-10 w-full h-full overflow-y-auto">
                <Outlet />
            </div>
        </div>
    );
}