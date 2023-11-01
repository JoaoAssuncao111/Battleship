import * as React from 'react'
import { useState, useEffect } from 'react';

export function Timer({ deadline, timeoutFunc }) {
  const [timeLeft, setTimeLeft] = useState<number | null>(null);

  useEffect(() => {
    const intervalId = setInterval(() => {
      const deadlineDate = new Date(deadline);
      const currentDate = new Date();
      const timeLeft = Math.max(deadlineDate.getTime() - currentDate.getTime(), 0);
      setTimeLeft(timeLeft);
    }, 1000);

    return () => clearInterval(intervalId);
  }, [deadline]);

  if (timeLeft === null) {
    return <div>Loading...</div>;
  }

  if (timeLeft === 0) {
    timeoutFunc()
    return <div>Time's up!</div>;
  }

  const minutes = Math.floor((timeLeft / (1000 * 60)) % 60);
  const seconds = Math.floor(((timeLeft / 1000) % 60));

  return (
    <div>
       {minutes} minutes and {seconds} seconds left 
    </div>
  );
}
