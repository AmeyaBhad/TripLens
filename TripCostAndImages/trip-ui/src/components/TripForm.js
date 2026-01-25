import { useState } from 'react';
import axios from 'axios';
import './TripForm.css';
import DestinationImages from './DestinationImages';

function TripForm() {
  const [source, setSource] = useState('');
  const [destination, setDestination] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const calculate = async () => {
    if (!source || !destination) {
      setError('Please enter both source and destination');
      return;
    }

    setError('');
    setLoading(true);

    try {
      const res = await axios.post('http://localhost:8080/api/trip/calculate', {
        source,
        destination,
      });
      setResult(res.data);
    } catch (err) {
      setError('Unable to calculate trip. Try valid locations.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <h1>Trip Cost Calculator</h1>

      <div className="form">
        <input
          type="text"
          placeholder="Source (e.g. Pune)"
          value={source}
          onChange={(e) => setSource(e.target.value)}
        />

        <input
          type="text"
          placeholder="Destination (e.g. Mumbai)"
          value={destination}
          onChange={(e) => setDestination(e.target.value)}
        />

        <button onClick={calculate} disabled={loading}>
          {loading ? 'Calculating...' : 'Calculate Trip'}
        </button>

        {error && <p className="error">{error}</p>}
      </div>

      {result && (
        <div className="result">
          <h2>Trip Details</h2>
          <p>
            <strong>Distance:</strong> {result.distanceKm.toFixed(2)} km
          </p>

          <div className="pricing">
            <div className="card cheap">
              <h3>Cheapest</h3>
              <p>₹{result.pricing.cheapest.toFixed(0)}</p>
              <span>Students</span>
            </div>

            <div className="card medium">
              <h3>Medium</h3>
              <p>₹{result.pricing.medium.toFixed(0)}</p>
              <span>Family (4–5)</span>
            </div>

            <div className="card luxury">
              <h3>Luxury</h3>
              <p>₹{result.pricing.luxury.toFixed(0)}</p>
              <span>Premium</span>
            </div>
          </div>
          <DestinationImages destination={destination} />
        </div>
      )}
    </div>
  );
}

export default TripForm;
