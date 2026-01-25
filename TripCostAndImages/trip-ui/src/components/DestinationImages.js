import { useState } from 'react';
import axios from 'axios';
import './DestinationImages.css';

function DestinationImages({ destination }) {
  const [images, setImages] = useState([]);
  const [show, setShow] = useState(false);
  const [loading, setLoading] = useState(false);

  const fetchImages = async () => {
    if (!destination) return;

    setShow(true);
    setLoading(true);

    try {
      const response = await axios.get('https://en.wikipedia.org/w/api.php', {
        params: {
          action: 'query',
          format: 'json',
          prop: 'pageimages',
          generator: 'search',
          gsrsearch: destination,
          gsrlimit: 6,
          pithumbsize: 300,
          origin: '*',
        },
      });

      const pages = response.data.query?.pages || {};
      const imgs = Object.values(pages)
        .filter((p) => p.thumbnail)
        .map((p) => p.thumbnail.source);

      setImages(imgs);
    } catch (err) {
      console.error('Image fetch failed', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="image-section">
      <button className="image-btn" onClick={fetchImages}>
        Show Images
      </button>

      {loading && <p>Loading images...</p>}

      {show && !loading && (
        <div className="image-grid">
          {images.length > 0 ? (
            images.map((url, index) => (
              <img key={index} src={url} alt={destination} />
            ))
          ) : (
            <p>No images found.</p>
          )}
        </div>
      )}
    </div>
  );
}

export default DestinationImages;
