import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { ToastService, Toast } from '../../core/services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.css'
})
export class ToastComponent implements OnInit, OnDestroy {
  toasts: (Toast & { removing?: boolean })[] = [];
  private sub!: Subscription;
  private toastSvc = inject(ToastService);

  ngOnInit(): void {
    this.sub = this.toastSvc.toast$.subscribe(t => {
      this.toasts.push(t);
      setTimeout(() => {
        const item = this.toasts.find(x => x.id === t.id);
        if (item) item['removing'] = true;
        setTimeout(() => { this.toasts = this.toasts.filter(x => x.id !== t.id); }, 300);
      }, 2800);
    });
  }

  ngOnDestroy(): void { this.sub?.unsubscribe(); }
}
