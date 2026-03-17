import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'relativeTime',
  standalone: true,
})
export class RelativeTimePipe implements PipeTransform {
  private readonly formatter = new Intl.RelativeTimeFormat('en', { numeric: 'auto' });

  transform(value: string | Date | null | undefined): string {
    if (!value) return '';

    const date = typeof value === 'string' ? new Date(value) : value;
    const now = new Date();
    const diffMs = date.getTime() - now.getTime();
    const diffSec = Math.round(diffMs / 1000);
    const diffMin = Math.round(diffSec / 60);
    const diffHour = Math.round(diffMin / 60);
    const diffDay = Math.round(diffHour / 24);
    const diffWeek = Math.round(diffDay / 7);
    const diffMonth = Math.round(diffDay / 30);
    const diffYear = Math.round(diffDay / 365);

    if (Math.abs(diffSec) < 60) {
      return this.formatter.format(diffSec, 'second');
    } else if (Math.abs(diffMin) < 60) {
      return this.formatter.format(diffMin, 'minute');
    } else if (Math.abs(diffHour) < 24) {
      return this.formatter.format(diffHour, 'hour');
    } else if (Math.abs(diffDay) < 7) {
      return this.formatter.format(diffDay, 'day');
    } else if (Math.abs(diffWeek) < 5) {
      return this.formatter.format(diffWeek, 'week');
    } else if (Math.abs(diffMonth) < 12) {
      return this.formatter.format(diffMonth, 'month');
    } else {
      return this.formatter.format(diffYear, 'year');
    }
  }
}
