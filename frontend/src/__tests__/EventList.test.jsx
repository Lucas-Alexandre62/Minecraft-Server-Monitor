import { describe, it, expect, beforeEach } from 'vitest';
import '@testing-library/jest-dom/vitest';
import { render, screen } from '@testing-library/react';
import EventList from '../components/EventList';

describe('EventList', () => {
  const defaultProps = {
    events: [],
    page: 0,
    totalPages: 0,
    totalElements: 0,
    onPageChange: () => {},
    filter: '',
    onFilterChange: () => {},
  };

  it('shows empty message when no events', () => {
    render(<EventList {...defaultProps} />);

    expect(
      screen.getByText('Nenhum evento registrado.')
    ).toBeInTheDocument();
  });

  it('renders event items when events exist', () => {
    const events = [
      {
        id: 1,
        type: 'SERVER_UP',
        createdAt: '2024-01-15T10:30:00',
      },
      {
        id: 2,
        type: 'SERVER_DOWN',
        createdAt: '2024-01-15T09:00:00',
      },
    ];

    render(
      <EventList
        {...defaultProps}
        events={events}
        totalPages={1}
        totalElements={2}
      />
    );

    expect(
      screen.getByText('Servidor online')
    ).toBeInTheDocument();

    expect(
      screen.getByText('Servidor offline')
    ).toBeInTheDocument();
  });

  it('shows filter buttons', () => {
    const events = [
      {
        id: 1,
        type: 'SERVER_UP',
        createdAt: '2024-01-15T10:30:00',
      },
    ];

    render(
      <EventList
        {...defaultProps}
        events={events}
        totalPages={1}
        totalElements={1}
      />
    );

    expect(
      screen.getByText('Todos')
    ).toBeInTheDocument();

    expect(
      screen.getByText('Online')
    ).toBeInTheDocument();

    expect(
      screen.getByText('Offline')
    ).toBeInTheDocument();
  });

  it('does not show pagination when only one page', () => {
    const events = [
      {
        id: 1,
        type: 'SERVER_UP',
        createdAt: '2024-01-15T10:30:00',
      },
    ];

    render(
      <EventList
        {...defaultProps}
        events={events}
        totalPages={1}
        totalElements={1}
      />
    );

    expect(
      screen.queryByText('← Anterior')
    ).not.toBeInTheDocument();
  });

  it('shows pagination when multiple pages', () => {
    const events = [
      {
        id: 1,
        type: 'SERVER_UP',
        createdAt: '2024-01-15T10:30:00',
      },
    ];

    render(
      <EventList
        {...defaultProps}
        events={events}
        page={0}
        totalPages={3}
        totalElements={30}
      />
    );

    expect(
      screen.getByText('← Anterior')
    ).toBeInTheDocument();

    expect(
      screen.getByText('Próxima →')
    ).toBeInTheDocument();

    expect(
      screen.getByText(/Página 1 de 3/)
    ).toBeInTheDocument();
  });
});
