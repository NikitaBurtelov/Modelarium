export default function ImagePreview({ files }) {
    if (!files?.length) return null;

    return (
        <div className="preview-grid">
            {files.map((file) => (
                <div className="preview-item" key={file.name + file.size}>
                    <img src={URL.createObjectURL(file)} alt={file.name} />
                </div>
            ))}
        </div>
    );
}